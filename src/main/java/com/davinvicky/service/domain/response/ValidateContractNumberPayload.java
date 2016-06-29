package nz.co.acc.myacc.services.invoicing.domain.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(description = "Response to submission to validate Contract number")
public class ValidateContractNumberPayload implements Serializable {

    @ApiModelProperty(value = "Whether the submitted Contract number was valid or not")
    private Boolean valid;

    public ValidateContractNumberPayload(boolean valid) {
        this.valid = valid;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    @Override
    public String toString() {
        return "ValidateContractNumberPayload{" +
                "valid=" + valid +
                '}';
    }
}
