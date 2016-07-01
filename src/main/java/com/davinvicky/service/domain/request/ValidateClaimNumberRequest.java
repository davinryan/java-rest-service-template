package com.davinvicky.service.domain.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.davinvicky.common.domain.request.Request;
import org.hibernate.validator.constraints.NotBlank;

@ApiModel(description = "Request to validate an ACC Claim/Medical Fees number")
public class ValidateClaimNumberRequest extends Request {

    @ApiModelProperty(value = "ACC Medical Fees number. Format = [A-Z0-9]*. Max = 12", required = true)
//    @Pattern(regexp = "[A-Z0-9]*")
//    @Size(min = 1, max = 12)
    @NotBlank
    private String claimNumber;

    public String getClaimNumber() {
        return claimNumber;
    }

    public void setClaimNumber(String claimNumber) {
        this.claimNumber = claimNumber;
    }

    @Override
    public String toString() {
        return "ValidateClaimNumberRequest{" +
                "claimNumber='" + claimNumber + '\'' +
                '}';
    }
}
