package com.davinvicky.service.domain.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.davinvicky.common.domain.request.Request;
import org.hibernate.validator.constraints.NotBlank;
import org.joda.time.DateTime;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@ApiModel(description = "Request to validate service code in conjunction with service date and contract number")
public class ValidateServiceCodeRequest extends Request {

    @ApiModelProperty(value = "The identification of a service provided as all or part of the treatment. Format = [A-Z0-9]*. Max = 10", required = true)
    @NotBlank
//    @Size(min = 1, max = 10)
//    @Pattern(regexp = "[A-Z0-9]*")
    private String serviceCode;

    @ApiModelProperty(value = "The date the provider performed the treatment. Format = yyyy-MM-dd'T'HH:mm:ss.SSSZ", required = true)
    @NotNull
    private DateTime serviceDate;

    @ApiModelProperty(value = "ACC Contract identifier. Format = [^a-z]*. Max = 8")
    @Size(max = 8)
    @Pattern(regexp = "[^a-z]*")
    private String contractNumber;

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    public DateTime getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(DateTime serviceDate) {
        this.serviceDate = serviceDate;
    }

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    @Override
    public String toString() {
        return "ValidateServiceCodeRequest{" +
                "serviceCode='" + serviceCode + '\'' +
                ", serviceDate=" + serviceDate +
                ", contractNumber='" + contractNumber + '\'' +
                '}';
    }
}
