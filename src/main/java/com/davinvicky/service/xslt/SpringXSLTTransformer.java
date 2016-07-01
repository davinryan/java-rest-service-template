package com.davinvicky.service.xslt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.davinvicky.common.logging.LogUtil;
import com.davinvicky.service.domain.request.CreateInvoiceFormRequest;
import com.davinvicky.service.util.XmlObjectMapper;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * XSLT parser to change provider invoices into MFP Schedules.
 */
@Component
public class SpringXSLTTransformer implements XSLTTransformer {

    @Value("#{'${invoicing.spring.xslt.createInvoice.path:Not Set}'}")
    private String xsltCreateInvoiceTemplatePath;

    private ClassPathResource xsltCreateInvoiceTemplateResource;

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringXSLTTransformer.class.getName());

    @Autowired
    private XmlObjectMapper xmlMapper;

    /**
     * {@inheritDoc}.
     */
    @Override
    public String generateMFPSchedule(CreateInvoiceFormRequest invoice) {
        // Indicate when main processing has finished
        invoice.setEndOfRunDateTime(new DateTime());

        // Transform Pojo
        String invoiceAsXML = convertPojoToXml(invoice);

        TransformerFactory factory = TransformerFactory.newInstance();
        StreamSource xsltStream = new StreamSource(getXsltCreateInvoiceTemplate());
        StringReader xmlString = new StringReader(invoiceAsXML);
        StreamSource inToTransform = new StreamSource(xmlString);
        StringWriter outFromWriter = new StringWriter();
        StreamResult outFromTransform = new StreamResult(outFromWriter);
        try {
            Transformer transformer = factory.newTransformer(xsltStream);
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(inToTransform, outFromTransform);
            return outFromWriter.toString();
        } catch (Exception e) {
            LOGGER.error("Failed to convert invoice: " + invoice + " to MFPSchedule XML", e);
        }
        return null;
    }

    private String convertPojoToXml(CreateInvoiceFormRequest invoice) {
        String invoiceAsXML = null;
        try {
            invoiceAsXML = xmlMapper.writeValueAsString(invoice);
            LogUtil.logDebugMsg(LOGGER, "XML version of JSON Provider Invoice", "\n" + invoiceAsXML);
        } catch (JsonProcessingException e) {
            LOGGER.error("Something when wrong while trying to convert invoice from json to xml", e);
        }
        return invoiceAsXML;
    }

    private InputStream getXsltCreateInvoiceTemplate() {
        if (xsltCreateInvoiceTemplateResource == null) {
            xsltCreateInvoiceTemplateResource = new ClassPathResource(xsltCreateInvoiceTemplatePath);
        }
        try {
            return xsltCreateInvoiceTemplateResource.getInputStream();
        } catch (IOException e) {
            LOGGER.error("Failed to look up xsl file for converting CreateInvoice into an MFPSchedule.", e);
            return null;
        }
    }
}
