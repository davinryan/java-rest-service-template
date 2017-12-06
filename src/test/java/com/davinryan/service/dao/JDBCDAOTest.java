package com.davinryan.service.dao;

import com.davinryan.common.restservice.domain.response.Response;
import com.davinryan.service.ServiceTemplate;
import com.davinryan.service.domain.request.ValidateVendorRequest;
import com.davinryan.service.domain.response.ValidateVendorIdPayload;
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

public class JDBCDAOTest {

    @Mock
    private JDBCDAO jdbcdao;


    @InjectMocks
    private ServiceTemplate service;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testValidateVendorID() throws Exception {
        // Setup
        ValidateVendorRequest validateVendorRequest = buildVendorID("Happy Health", "HHACC");

        // Expectations
        when(jdbcdao.validateVendorId(validateVendorRequest.getVendorId())).thenReturn(new ValidateVendorIdPayload(true, true));

        // Test
        Response result = service.validateVendorId(validateVendorRequest);

        // Verify
        assertThat(result.getPayload().toString(), is("ValidateVendorIdPayload{authorised=true, active=true}"));

    }

    @Test
    public void testFailToValidateVendorID() throws Exception {
        // Setup
        ValidateVendorRequest validateVendorRequest = buildVendorID("Sad bad sad", "HHACC");
        //Given
        given(jdbcdao.validateVendorId(validateVendorRequest.getVendorId())).willThrow(new DatabaseException("Mock Exception", new SQLException()));

        // Test
        try {
            Response result = service.validateVendorId(validateVendorRequest);

        } catch (DatabaseException e) {
            assertThat(e.getMessage(), is("Mock Exception"));
        }


        // Verify
        //   assertThat(result.getPayload().toString(), is("ValidateVendorIdPayload{authorised=true}"));

    }

    private ValidateVendorRequest buildVendorID(String orgName, String vendorID) {
        ValidateVendorRequest vendor = new ValidateVendorRequest();
        vendor.setVendorId(vendorID);
        vendor.setCorrelationId("12345");
        return vendor;
    }
}
