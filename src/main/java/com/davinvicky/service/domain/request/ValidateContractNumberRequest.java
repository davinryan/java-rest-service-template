package com.davinvicky.service.domain.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.davinvicky.common.domain.request.Request;
import org.hibernate.validator.constraints.NotBlank;

@ApiModel(description = "Request to validate an ACC Contract number/identifier")
public class ValidateContractNumberRequest extends Request {

    @ApiModelProperty(value = "ACC Contract identifier. Format = [^a-z]*. Max = 8", required = true)
    @NotBlank
//    @Size(max = 8)
//    @Pattern(regexp = "[^a-z]*")
    private String contractNumber;

    public String getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(String contractNumber) {
        this.contractNumber = contractNumber;
    }

    @Override
    public String toString() {
        return "ValidateContractNumberRequest{" +
                "contractNumber='" + contractNumber + '\'' +
                '}';
    }
}
