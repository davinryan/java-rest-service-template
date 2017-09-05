package com.davinvicky.service;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.davinvicky.common.domain.response.Response;
import com.davinvicky.common.domain.response.SuccessResponse;
import com.davinvicky.common.exception.UnauthorisedAccessException;
import com.davinvicky.common.jee.JeeUtils;
import com.davinvicky.common.logging.LogServiceCallWithMDC;
import com.davinvicky.common.logging.RedactUtil;
import com.davinvicky.service.dao.DatabaseException;
import com.davinvicky.service.dao.JDBCDAO;
import com.davinvicky.service.dao.JMSDao;
import com.davinvicky.service.domain.request.*;
import com.davinvicky.service.domain.response.*;
import com.davinvicky.service.xslt.XSLTTransformer;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.jms.JMSException;
import javax.validation.Valid;

import static com.davinvicky.common.logging.LogUtil.logDebugMsg;
import static com.davinvicky.service.LOGGING_MARKERS.PORTLET;

/**
 * Invoicing REST Service.
 *
 */

@Controller
@RequestMapping(value = "/")
@Api(value = "/main", description = "Main Service")
public class ServiceTemplate {

    private static final String INFO = "info";

    @Autowired
    private JDBCDAO jdbcdao;

    @Autowired
    private XSLTTransformer XSLTTransformer;

    @Autowired
    private JMSDao JMSDao;

    @Value("#{'${swagger.version:Not Set}'}")
    private String version;

    private final Boolean disableLogRedactedMessages;

    public ServiceTemplate() {
        disableLogRedactedMessages = Boolean.parseBoolean(new JeeUtils().jndiLookup("serviceTemplate.disableLogRedactedMessages", "false"));
    }

    /**
     * This has been made non static and non final for testing purposes only.
     */
    private Logger LOGGER = LoggerFactory.getLogger(ServiceTemplate.class); // NOSONAR

    @RequestMapping(
            value = "/",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Smoke test to check Provider Invoicing service is up")
    @ResponseBody
    public Response applicationStart() throws JMSException, DatabaseException {
        return new SuccessResponse("Hello from the Provider Invoicing Service.... No SERVICE LEFT BEHIND!!!!");
    }

    @RequestMapping(
            value = "/status",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_PLAIN_VALUE)
    @ApiOperation(
            value = "Status check to test whether Provider Invoicing service is up")
    @Cacheable(value = "healthCheckCache", key = "'healthCheck'") // Cache to prevent DOS attacks on this method as it is expensive.
    @ResponseBody
    public String healthCheck() throws JMSException, DatabaseException, UnauthorisedAccessException {
        jdbcdao.healthCheck();
        JMSDao.healthCheck();
        return version;
    }

    @RequestMapping(
            value = "/validateSomeId",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Validates if a some id is unique in a valid format.")
    @ResponseBody
    @LogServiceCallWithMDC
    public Response validateSomeId(@Valid @RequestBody ValidateProviderRequest request) throws DatabaseException {
        ValidateProviderIdPayload payload = jdbcdao.validateProviderId(request.getProviderId());
        return new SuccessResponse(payload);
    }

    @RequestMapping(
            value = "/invoices",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Submit Provider Invoice")
    @ResponseBody
    @LogServiceCallWithMDC
    public Response submitInvoice(@Valid @RequestBody CreateInvoiceFormRequest invoice) throws JMSException, DatabaseException { //NOSONAR
        logDebugMsg(LOGGER, "Incoming Create Invoice Request as JSON", invoice.toString());

        // Populate derived fields
        prepopulateDerivedFieldsForMFP(invoice);

        // Persist new schedule to database
        jdbcdao.saveInvoiceRequest(invoice);

        // Generate xml
        String xml = XSLTTransformer.generateMFPSchedule(invoice);

        // Send xml to mq
        JMSDao.send(xml);

        // Log invoice to the command line
        logRedactedObj(invoice);

        return new SuccessResponse(new SubmitInvoicePayload(invoice.getInvoiceNumber()));
    }

    @RequestMapping(
            value = "/log",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Logs an error server side on behalf of the client",
            notes = "Javascript client side applications can only log locally on a client computer. This means we can " +
                    "loose important error logging. The interface allows a client application to log errors on the " +
                    "server side")
    @ResponseBody
    @LogServiceCallWithMDC
    public Response log(@Valid @RequestBody ClientLogEntryRequest logEntry) {
        String msg = "Logging captured from remote client: " + logEntry.getDetails()
                + " while trying to use the " + logEntry.getDetails().getApplication()
                + " application.";
        if (logEntry.getLogLevel().equalsIgnoreCase(INFO)) {
            LOGGER.info(PORTLET.getMarker(), msg);
        } else {
            LOGGER.error(PORTLET.getMarker(), msg);
        }
        return new SuccessResponse("Thank you, you're logEntry was successfully logged.");
    }
}
