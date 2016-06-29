package nz.co.acc.myacc.services.invoicing.dao;

import nz.co.acc.myacc.common.domain.response.Response;
import nz.co.acc.myacc.services.invoicing.InvoicingService;
import nz.co.acc.myacc.services.invoicing.domain.request.GetInvoiceNumberRequest;
import nz.co.acc.myacc.services.invoicing.domain.request.ValidateVendorRequest;
import nz.co.acc.myacc.services.invoicing.domain.response.InvoiceNumberPayload;
import nz.co.acc.myacc.services.invoicing.domain.response.ValidateVendorIdPayload;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;

public class InvoicingJDBCDAOTest {

    @Mock
    private InvoicingJDBCDAO invoicingJDBCDAO;


    @InjectMocks
    private InvoicingService service;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testValidateVendorID() throws Exception {
        // Setup
        ValidateVendorRequest validateVendorRequest = buildVedorID("Happy Health", "HHACC");

        // Expectations
        when(invoicingJDBCDAO.validateVendorId(validateVendorRequest.getVendorId(), validateVendorRequest.getOrganisationId())).thenReturn(new ValidateVendorIdPayload(true, true));

        // Test
        Response result = service.validateVendorId(validateVendorRequest);

        // Verify
        assertThat(result.getPayload().toString(), is("ValidateVendorIdPayload{authorised=true, active=true}"));

    }

    @Test
    public void testFailToValidateVendorID() throws Exception {
        // Setup
        ValidateVendorRequest validateVendorRequest = buildVedorID("Sad bad sad", "HHACC");
        //Given
        given(invoicingJDBCDAO.validateVendorId(validateVendorRequest.getVendorId(), validateVendorRequest.getOrganisationId())).willThrow(new DatabaseException("Mock Exception", new SQLException()));

        // Expectations
//        when(invoicingJDBCDAO.validateVendorId(validateVendorRequest.getVendorId(), validateVendorRequest.getOrganisationId())).thenReturn(new ValidateVendorIdPayload(true));

        // Test
        try {
            Response result = service.validateVendorId(validateVendorRequest);

        } catch (DatabaseException e) {
            assertThat(e.getMessage(), is("Mock Exception"));
        }


        // Verify
        //   assertThat(result.getPayload().toString(), is("ValidateVendorIdPayload{authorised=true}"));

    }

    private ValidateVendorRequest buildVedorID(String orgName, String vendorID) {
        ValidateVendorRequest vendor = new ValidateVendorRequest();
        vendor.setOrganisationId(orgName);
        vendor.setVendorId(vendorID);
        vendor.setCorrelationId("12345");
        return vendor;
    }

    @Test
    public void testGetInvoiceNumber() throws Exception {
        // Setup
        GetInvoiceNumberRequest getInvoiceNumberRequest = new GetInvoiceNumberRequest();
        getInvoiceNumberRequest.setCorrelationId("12345");
        getInvoiceNumberRequest.setType("ACC047e");

        // Expectations
        when(invoicingJDBCDAO.getInvoiceNumber()).thenReturn("ABCDE12345");

        // Test
        Response result = service.getInvoiceNumber(getInvoiceNumberRequest);
        System.out.println(result.getPayload());
        String formattedDate = DateTime.now().toString("yyyy-MM-dd");
        // Verify
        assertThat(result.getPayload().toString(), is("InvoiceNumberPayload{invoiceNumber='ABCDE12345', currentDate="+formattedDate+"}"));

    }

}
