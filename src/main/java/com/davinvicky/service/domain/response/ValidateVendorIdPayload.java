package nz.co.acc.myacc.services.invoicing.domain.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(description = "Response to submission to validate Vendor Id")
public class ValidateVendorIdPayload implements Serializable {

    @ApiModelProperty(value = "Whether the submitted Vendor Id is authorised or not")
    private final Boolean authorised;

    @ApiModelProperty(value = "Whether the submitted Vendor Id is active or not")
    private final Boolean active;

    public ValidateVendorIdPayload(Boolean authorised, Boolean active) {
        this.authorised = authorised;
        this.active =active;
    }

    public Boolean getActive() {
        return active;
    }

    public Boolean getAuthorised() {
        return authorised;
    }

    @Override
    public String toString() {
        return "ValidateVendorIdPayload{" +
                "authorised=" + authorised +
                ", active=" + active +
                '}';
    }
}
