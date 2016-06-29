package nz.co.acc.myacc.services.invoicing.domain.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

@ApiModel(description = "Response to submission to validate Provider Id")
public class ValidateProviderIdPayload implements Serializable {

    @ApiModelProperty(value = "Whether the submitted Provider Id was valid or not")
    private final Boolean valid;

    public ValidateProviderIdPayload(Boolean valid) {
        this.valid = valid;
    }

    public Boolean getValid() {
        return valid;
    }

    @Override
    public String toString() {
        return "ValidateProviderIdPayload{" +
                "valid=" + valid +
                '}';
    }
}
