package nz.co.acc.myacc.services.invoicing.domain.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(description = "Response to submission to validate Service code")
public class ValidateServiceCodePayload implements Serializable {

    @ApiModelProperty(value = "Whether the submitted Service code was valid or not")
    private Boolean valid;

    public ValidateServiceCodePayload(Boolean valid) {
        this.valid = valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public Boolean getValid() {
        return valid;
    }

    @Override
    public String toString() {
        return "ValidateServiceCodePayload{" +
                "valid=" + valid +
                '}';
    }
}
