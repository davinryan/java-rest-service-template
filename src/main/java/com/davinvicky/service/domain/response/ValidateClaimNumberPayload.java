package nz.co.acc.myacc.services.invoicing.domain.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(description = "Response to submission to validate Claim number")
public class ValidateClaimNumberPayload implements Serializable {

    @ApiModelProperty(value = "Whether the submitted Claim number was valid or not")
    private final Boolean valid;

    public ValidateClaimNumberPayload(Boolean valid) {
        this.valid = valid;
    }

    public Boolean getValid() {
        return valid;
    }

    @Override
    public String toString() {
        return "ValidClaimNumberPayload2{" +
                "valid=" + valid +
                '}';
    }
}
