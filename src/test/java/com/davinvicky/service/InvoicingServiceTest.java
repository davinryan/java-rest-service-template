package nz.co.acc.myacc.services.invoicing;

import nz.co.acc.myacc.common.domain.response.Response;
import nz.co.acc.myacc.services.invoicing.dao.DatabaseException;
import nz.co.acc.myacc.services.invoicing.dao.InvoicingJDBCDAO;
import nz.co.acc.myacc.services.invoicing.dao.InvoicingMQDao;
import nz.co.acc.myacc.services.invoicing.domain.request.ClientLogEntryRequest;
import nz.co.acc.myacc.services.invoicing.domain.request.CreateInvoiceFormRequest;
import nz.co.acc.myacc.services.invoicing.xslt.InvoicingXSLT;
import nz.co.acc.myacc.services.test.util.builders.CreateInvoiceFormBuilder;
import nz.co.acc.myacc.services.test.util.builders.ServiceDetailsBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import javax.jms.JMSException;

import static nz.co.acc.myacc.services.invoicing.LOGGING_MARKERS.PORTLET;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.verify;

public class InvoicingServiceTest {

    @Mock
    private InvoicingJDBCDAO invoicingJDBCDAOMock;

    @Mock
    private InvoicingXSLT invoicingXSLT;

    @Mock
    private InvoicingMQDao invoicingMQDao;

    @Mock
    private Logger LOGGER;

    @InjectMocks
    private InvoicingService service;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testLogWithLevelError() {
        // Setup
        ClientLogEntryRequest logEntry = buildClientLogEntry("error");

        // Test
        Response result = service.log(logEntry);

        // Verify
        assertThat(result.getPayload().toString(), is("Thank you, you're logEntry was successfully logged."));

        String expectedErrorMsg = "Logging captured from remote client: " + logEntry.getDetails()
                + " while trying to use the " + logEntry.getDetails().getApplication()
                + " application.";
        verify(LOGGER).error(PORTLET.getMarker(), expectedErrorMsg);
    }

    @Test
    public void testLogWithLevelInfo() {
        // Setup
        ClientLogEntryRequest logEntry = buildClientLogEntry("info");

        // Test
        Response result = service.log(logEntry);

        // Verify
        assertThat(result.getPayload().toString(), is("Thank you, you're logEntry was successfully logged."));

        String expectedErrorMsg = "Logging captured from remote client: " + logEntry.getDetails()
                + " while trying to use the " + logEntry.getDetails().getApplication()
                + " application.";
        verify(LOGGER).info(PORTLET.getMarker(), expectedErrorMsg);
    }

    private ClientLogEntryRequest buildClientLogEntry(String logLevel) {
        ClientLogEntryRequest logEntry = new ClientLogEntryRequest();
        logEntry.setLogLevel(logLevel);
        ClientLogEntryRequest.Details details = new ClientLogEntryRequest.Details();
        details.setApplication("Test Application");
        details.setMessage("Some Stacktrace");
        details.setUserAgent("Some browser and operating system");
        details.setUserId("testUser01");
        logEntry.setDetails(details);
        return logEntry;
    }

    @Test
    public void testMassageAndPrepopulateDerivedFieldsForMFP() throws DatabaseException, JMSException {
        // Setup Invoice with two service codes whose minutes total 60 minutes.
        CreateInvoiceFormBuilder invoiceBuilder = new CreateInvoiceFormBuilder();
        CreateInvoiceFormRequest invoice = invoiceBuilder.createValid().build();
        ServiceDetailsBuilder detailsBuilder = new ServiceDetailsBuilder();
        detailsBuilder.createServiceCodeWithFeeBasedOnMinutes(30);
        detailsBuilder.createServiceCodeWithFeeBasedOnMinutes(30);
        invoice.setServiceDetails(detailsBuilder.buildAsList());

        // Test
        service.submitProviderInvoice(invoice);

        // Verify That minutes are reset to zero and hours bumped up to 1
        assertThat(invoice.getServiceDetails().get(0).getTotalFeeBasedOnTimeHours(), is(1));
        assertThat(invoice.getServiceDetails().get(0).getTotalFeeBasedOnTimeMinutes(), is(0));

    }
}
