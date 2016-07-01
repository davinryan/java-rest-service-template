package com.davinvicky.service.domain.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.davinvicky.common.domain.request.Request;
import org.hibernate.validator.constraints.NotBlank;

@ApiModel(description = "Request to validate ACC provider identifier")
public class ValidateProviderRequest extends Request {

    @ApiModelProperty(value = "The standard ACC identifier of the provider. Format = [A-Z0-9]*. Max = 8", required = true)
    @NotBlank
//    @Size(max = 8)
//    @Pattern(regexp = "[A-Z0-9]*")
    private String providerId;

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    @Override
    public String toString() {
        return "ValidateProviderRequest{" +
                "providerId='" + providerId + '\'' +
                '}';
    }
}
