package nz.co.acc.myacc.services.invoicing.xslt;

import com.fasterxml.jackson.databind.ObjectMapper;
import nz.co.acc.myacc.services.invoicing.domain.request.CreateInvoiceFormRequest;
import org.apache.commons.io.IOUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;
import java.nio.charset.Charset;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration({"classpath:dispatcher-servlet-test.xml"})
public class InvoicingSpringXSLTTest {

    @Autowired
    private InvoicingXSLT invoicingXSLT;

    @Autowired
    private ObjectMapper jsonMapper;

//    @Autowired
//    private XmlMapper xmlMapper;

    @Ignore
    @Test
    public void convertCreateInvoiceBeanToMFPeScheduleXMLTest() throws IOException {
        String inputJsonPath = "nz/co/acc/myacc/services/invoicing/xslt/InvoicingSpringXSLTTest-input.json";
        String outputXMLPath = "nz/co/acc/myacc/services/invoicing/xslt/InvoicingSpringXSLTTest-expectedOutput.xml";
        assertInputJsonCreateOutputMFPScheduleXML(inputJsonPath, outputXMLPath);

    }

    private void assertInputJsonCreateOutputMFPScheduleXML(String inputJsonPath, String outputXMLPath) throws IOException {
        // Setup
        ClassPathResource jsonInput = new ClassPathResource(inputJsonPath);
        CreateInvoiceFormRequest inputRequest = jsonMapper.readValue(jsonInput.getInputStream(), CreateInvoiceFormRequest.class);
//        System.out.println("XML before xslt transformation");
//        xmlMapper.writeValueAsString(inputRequest);

        // Test
        String mfpSchedule = invoicingXSLT.generateMFPSchedule(inputRequest);

        // Verify
        ClassPathResource expectedMfpScheduleResource = new ClassPathResource(outputXMLPath);
        String expectedMfpSchedule = IOUtils.toString(expectedMfpScheduleResource.getInputStream(), Charset.forName("UTF-8"));
        mfpSchedule = removeWhitespaceLineReturnsMFPSchemas(mfpSchedule);
        expectedMfpSchedule = removeWhitespaceLineReturnsMFPSchemas(expectedMfpSchedule);
        assertThat(mfpSchedule, is(expectedMfpSchedule));
    }

    private String removeWhitespaceLineReturnsMFPSchemas(String xml) {
        xml = xml.replaceAll("\r\n", "");
        xml = xml.replaceAll(">\\s+<", "><");
        xml = xml.replaceAll("\\s+", " ");
        xml = xml.replaceAll("<MFPSchedule[\\s+\\w+\\:\\=\\\\\\\"\\/.\\-]*>", "<MFPSchedule>");
        return xml.trim();
    }
}