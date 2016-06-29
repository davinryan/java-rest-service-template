package nz.co.acc.myacc.services.invoicing.domain.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import nz.co.acc.myacc.common.domain.request.Request;
import org.hibernate.validator.constraints.NotBlank;

/**
 * @Author Iain Lumsden
 * Pojo that represents a JSON request to the VendorAuthorised API
 * Created by lumsdei on 06/04/2016.
 */


@ApiModel(value = "Request to validate Vendor's ACC identifier")
public class ValidateVendorWithOrgNameRequest extends Request {

    @ApiModelProperty(value = "Vendor's ACC identifier. Format = [^a-z]*. Max = 12", required = true)
//    @Pattern(regexp = "[^a-z]*")
//    @Size(max = 12)
    @NotBlank
    private String vendorId;

    @ApiModelProperty(value = "ACC Identifier for provider organisation", required = true)
    @NotBlank
    private String organisationName;

    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    @Override
    public String toString() {
        return "ValidateVendorWithOrgNameRequest{" +
                "vendorId='" + vendorId + '\'' +
                ", organisationName='" + organisationName + '\'' +
                '}';
    }
}
