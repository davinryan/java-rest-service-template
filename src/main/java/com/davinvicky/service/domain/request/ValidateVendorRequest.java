package com.davinvicky.service.domain.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.davinvicky.common.domain.request.Request;
import org.hibernate.validator.constraints.NotBlank;


/**
 * @Author Iain Lumsden
 * Pojo that represents a JSON request to the VendorAuthorised API
 * Created by lumsdei on 06/04/2016.
 */


@ApiModel(value = "Request to validate Vendor's ACC identifier")
public class ValidateVendorRequest extends Request {

    @ApiModelProperty(value = "Vendor's ACC identifier. Format = [^a-z]*. Max = 12", required = true)
    @NotBlank
    private String vendorId;

    @ApiModelProperty(value = "ACC Identifier for provider organisation", required = true)
    @NotBlank
    private String organisationId;

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getOrganisationId() {
        return organisationId;
    }

    public void setOrganisationId(String organisationId) {
        this.organisationId = organisationId;
    }

    @Override
    public String toString() {
        return "ValidateVendorRequest{" +
                "vendorId='" + vendorId + '\'' +
                ", organisationId='" + organisationId + '\'' +
                '}';
    }
}
