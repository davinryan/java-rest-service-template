package com.davinvicky.service.test.util.builders;

import com.davinvicky.service.domain.request.ServiceCode;

import java.util.ArrayList;
import java.util.List;

public class ServiceCodeBuilder {
    private ServiceCode serviceCode = new ServiceCode();

    public ServiceCodeBuilder createValid() {
        createServiceCodeFeeBasedOnDistance(23);
        return this;
    }

    public ServiceCodeBuilder createServiceCodeFeeBasedOnDistance(Integer distance) {
        serviceCode.setCode("CODE");
        serviceCode.setFeeBasedOnDistance(distance);
        return this;
    }

    public ServiceCodeBuilder createServiceCodeFeeBasedOnMinutes(Integer hours) {
        serviceCode.setCode("CODE");
        serviceCode.setFeeBasedOnTimeMinutes(hours);
        return this;
    }

    public ServiceCode build() {
        return serviceCode;
    }

    public ServiceCodeBuilder clearAllFeeBasedOn() {
        serviceCode.setFeeBasedOnDistance(null);
        serviceCode.setFeeBasedOnTimeHours(null);
        serviceCode.setFeeBasedOnTimeMinutes(null);
        serviceCode.setFeeBasedOnUnits(null);
        return this;
    }

    public ServiceCodeBuilder createServiceCodeFeeBasedOnHours(Integer minutes) {
        serviceCode.setCode("CODE");
        serviceCode.setFeeBasedOnTimeHours(minutes);
        return this;
    }

    public List<ServiceCode> buildAsList() {
        List<ServiceCode> serviceDetails = new ArrayList<ServiceCode>();
        serviceDetails.add(serviceCode);
        return serviceDetails;
    }
}
