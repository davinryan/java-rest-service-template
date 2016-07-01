package com.davinvicky.service.test.util.builders;

import com.davinvicky.service.domain.request.ServiceDetails;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Builder for {@link ServiceDetails}.
 */
public class ServiceDetailsBuilder {
    private static DateTimeFormatter DATE_FORMAT_STRING = DateTimeFormat.forPattern("dd/MM/yyyy");

    private ServiceDetails details = new ServiceDetails();

    public ServiceDetailsBuilder createValid() {
        details.setProviderId("PROVIDER");
        details.setServiceDate(DateTime.parse("11/04/2016", DATE_FORMAT_STRING));
        details.setFee(new BigDecimal(1234, new MathContext(2, RoundingMode.CEILING)));
        details.setServiceComments("Some Service Comments");
        addValidServiceCode();
        return this;
    }

    public ServiceDetailsBuilder addValidServiceCode() {
        details.getServiceCodes().add(new ServiceCodeBuilder().createValid().build());
        return this;
    }

    public ServiceDetailsBuilder createServiceCodeWithFeeBasedOnMinutes(Integer minutes) {
        details.getServiceCodes().add(new ServiceCodeBuilder().createServiceCodeFeeBasedOnMinutes(minutes).build());
        return this;
    }

    public ServiceDetailsBuilder createServiceCodeWithFeeBasedOnHours(Integer hours) {
        details.getServiceCodes().add(new ServiceCodeBuilder().createServiceCodeFeeBasedOnHours(hours).build());
        return this;
    }

    public ServiceDetails build() {
        return details;
    }

    public List<ServiceDetails> buildAsList() {
        List<ServiceDetails> serviceDetails = new ArrayList<ServiceDetails>();
        serviceDetails.add(details);
        return serviceDetails;
    }
}
