package nz.co.acc.myacc.services.invoicing;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import nz.co.acc.myacc.common.domain.response.Response;
import nz.co.acc.myacc.common.domain.response.SuccessResponse;
import nz.co.acc.myacc.common.exception.UnauthorisedAccessException;
import nz.co.acc.myacc.common.jee.JeeUtils;
import nz.co.acc.myacc.common.logging.LogServiceCallWithMDC;
import nz.co.acc.myacc.common.logging.RedactUtil;
import nz.co.acc.myacc.services.invoicing.dao.DatabaseException;
import nz.co.acc.myacc.services.invoicing.dao.InvoicingJDBCDAO;
import nz.co.acc.myacc.services.invoicing.dao.InvoicingMQDao;
import nz.co.acc.myacc.services.invoicing.domain.request.*;
import nz.co.acc.myacc.services.invoicing.domain.response.*;
import nz.co.acc.myacc.services.invoicing.xslt.InvoicingXSLT;
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

import static nz.co.acc.myacc.common.logging.LogUtil.logDebugMsg;
import static nz.co.acc.myacc.services.invoicing.LOGGING_MARKERS.PORTLET;

/**
 * Provider Invoicing REST Service.
 *
 * @author Stuart McKeown
 * @since 28/10/2015
 */

@Controller
@RequestMapping(value = "/")
@Api(value = "/invoicing", description = "Provider Invoicing Service")
public class InvoicingService {

    private static final String INFO = "info";

    @Autowired
    private InvoicingJDBCDAO egatewayDao;

    @Autowired
    private InvoicingXSLT invoicingXSLT;

    @Autowired
    private InvoicingMQDao invoicingMqDao;

    @Value("#{'${invoicing.swagger.version:Not Set}'}")
    private String version;

    private final Boolean disableLogRedactedMessages;

    public InvoicingService() {
        disableLogRedactedMessages = Boolean.parseBoolean(new JeeUtils().jndiLookup("providerInvoicingService.disableLogRedactedMessages", "false"));
    }

    /**
     * This has been made non static and non final for testing purposes only.
     */
    private Logger LOGGER = LoggerFactory.getLogger(InvoicingService.class); // NOSONAR

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
        egatewayDao.healthCheck();
        invoicingMqDao.healthCheck();
        return version;
    }

    @RequestMapping(
            value = "/validateProviderId",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Validates if a provider id is unique in a valid format.")
    @ResponseBody
    @LogServiceCallWithMDC
    public Response validateProviderId(@Valid @RequestBody ValidateProviderRequest request) throws DatabaseException {
        ValidateProviderIdPayload payload = egatewayDao.validateProviderId(request.getProviderId());
        return new SuccessResponse(payload);
//        return new SuccessResponse(new ValidateProviderIdPayload(true)); //NOSONAR
    }

//     Commented out for later use.
//    private void logRequestResponse(@Valid @RequestBody Object request, Object payload) {
//        LOGGER.info("Request: " + request);
//        LOGGER.info("Response: " + payload);
//    }

    @RequestMapping(
            value = "/validateContractNumber",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Validates if a contractNo exists and is active.")
    @ResponseBody
    @LogServiceCallWithMDC
    public Response validateContractNumber(@Valid @RequestBody ValidateContractNumberRequest request) throws DatabaseException {
        ValidateContractNumberPayload payload = egatewayDao.validateContractNumber(request.getContractNumber());
        return new SuccessResponse(payload);
//        return new SuccessResponse(new ValidateProviderIdPayload(true)); //NOSONAR
    }

    @RequestMapping(
            value = "/validateClaimNumber",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Validates if a contractNo exists and is active.")
    @ResponseBody
    @LogServiceCallWithMDC
    public Response validateClaimNumber(@Valid @RequestBody ValidateClaimNumberRequest request) throws DatabaseException {
        ValidateClaimNumberPayload payload = egatewayDao.validateClaimNumber(request.getClaimNumber());
        return new SuccessResponse(payload);
//        return new SuccessResponse(new ValidateClaimNumberPayload(true)); //NOSONAR
    }

    @RequestMapping(
            value = "/validateServiceCode",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Validates if a service code is valid for specified service date and contractNo.")
    @ResponseBody
    @LogServiceCallWithMDC
    public Response validateServiceCode(@Valid @RequestBody ValidateServiceCodeRequest request) throws DatabaseException {
        ValidateServiceCodePayload payload = egatewayDao.validateServiceCode(request.getServiceCode(), request.getServiceDate(), request.getContractNumber());
        return new SuccessResponse(payload);
//        return new SuccessResponse(new ValidateServiceCodePayload(true)); //NOSONAR
    }

    @RequestMapping(
            value = "/getInvoiceNumber",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Requests a new invoice number from the database")
    @ResponseBody
    @LogServiceCallWithMDC
    public Response getInvoiceNumber(@Valid @RequestBody GetInvoiceNumberRequest request) throws DatabaseException { //NOSONAR request is used by LogServiceCallWithMDC
        InvoiceNumberPayload payload = new InvoiceNumberPayload(egatewayDao.getInvoiceNumber());
        payload.setCurrentDate(new LocalDate());
        return new SuccessResponse(payload);
//        return new SuccessResponse(new InvoiceNumberPayload("abc123"));//NOSONAR
    }

    @RequestMapping(
            value = "/validateVendorId",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Validates if a vendor ID is unique in a valid format, and authorised for the Organisation using that certificate")
    @ResponseBody
    @LogServiceCallWithMDC
    public Response validateVendorId(@Valid @RequestBody ValidateVendorRequest request) throws DatabaseException {
        ValidateVendorIdPayload payload = egatewayDao.validateVendorId(request.getVendorId(), request.getOrganisationId());
        return new SuccessResponse(payload);
//        return new SuccessResponse(new ValidateVendorIdPayload(true, true)); //NOSONAR
    }

    /**
     * @param request {@link ValidateVendorWithOrgNameRequest}
     * @deprecated Please use validateVendorId as organisation names may not be unique.
     */
    @RequestMapping(
            value = "/validateVendorIdWithOrgName",
            method = RequestMethod.POST,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiOperation(
            value = "Validates if a vendor ID is unique in a valid format, and authorised for the Organisation using that certificate")
    @ResponseBody
    @LogServiceCallWithMDC
    @Deprecated
    public Response validateVendorWithOrgName(@Valid @RequestBody ValidateVendorWithOrgNameRequest request) throws DatabaseException {
        ValidateVendorIdPayload payload = egatewayDao.validateVendorIdWithOrgName(request.getVendorId(), request.getOrganisationName());
        return new SuccessResponse(payload);
//        return new SuccessResponse(new ValidateVendorIdPayload(true, true)); //NOSONAR
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
    public Response submitProviderInvoice(@Valid @RequestBody CreateInvoiceFormRequest invoice) throws JMSException, DatabaseException { //NOSONAR
        logDebugMsg(LOGGER, "Incoming Create Invoice Request as JSON", invoice.toString());

        // Populate derived fields
        prepopulateDerivedFieldsForMFP(invoice);

        // Persist new schedule to database
        egatewayDao.saveInvoiceRequest(invoice);

        // Generate eSchedule
        String newMFPSchedule = invoicingXSLT.generateMFPSchedule(invoice);

        // Send eSchedule to MFP
        invoicingMqDao.send(newMFPSchedule);

        // Log invoice to the command line
        logRedactedInvoice(invoice);

        return new SuccessResponse(new SubmitInvoicePayload(invoice.getInvoiceNumber()));
    }

    /**
     * Massage and populate derived information for MFP. If MFP was written properly we wouldn't have to do this!!
     *
     * @param invoice {@link CreateInvoiceFormRequest} to process
     * @throws DatabaseException when the database doesn't play ball.
     */
    private void prepopulateDerivedFieldsForMFP(CreateInvoiceFormRequest invoice) throws DatabaseException {
        // handle missing vendor id
        if (invoice != null && invoice.getVendorName() == null) {
            invoice.setVendorName(egatewayDao.getVendorName(invoice.getVendorId()));
        }
    }

    private void logRedactedInvoice(@Valid @RequestBody CreateInvoiceFormRequest invoice) {
        CreateInvoiceFormRequest invoiceToLog = invoice;
        if (!disableLogRedactedMessages) {
            invoiceToLog = RedactUtil.redactObject(invoice);
        }
        String newRedactedMFPSchedule = invoicingXSLT.generateMFPSchedule(invoiceToLog);
        LOGGER.info("Message contents: [\n" + newRedactedMFPSchedule + "]");
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
