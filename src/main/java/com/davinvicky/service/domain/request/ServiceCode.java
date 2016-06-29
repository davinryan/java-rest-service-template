package nz.co.acc.myacc.services.invoicing.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import nz.co.acc.myacc.common.validation.Field;
import nz.co.acc.myacc.common.validation.OnlyZeroOrOneOfTheFollowingFields;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * Created by RyanDa on 11/04/2016.
 * <p>
 * {
 * "providerID": "PROVIDER",
 * "serviceDate": "11/04/2016"
 * }
 */
@ApiModel(description = "Represents a single service code and its dependants")
@OnlyZeroOrOneOfTheFollowingFields(fields = {
        @Field(compositeFieldNames = {"feeBasedOnTimeHours", "feeBasedOnTimeMinutes"}),
        @Field(name = "feeBasedOnDistance"),
        @Field(name = "feeBasedOnUnits")})
public class ServiceCode {

    @ApiModelProperty(value = "The identification of a service provided as all or part of the treatment. Format = [A-Z0-9]*. Max = 10", required = true)
    @NotBlank
    @Size(min = 1, max = 10)
    @Pattern(regexp = "[A-Z0-9]*")
    private String code;

    @ApiModelProperty(value = "The service line total hours claimed")
    @JsonProperty(value = "hours")
    @DecimalMax(value = "99")
    private Integer feeBasedOnTimeHours;

    @ApiModelProperty(value = "The service line total minutes claimed")
    @JsonProperty(value = "minutes")
    @DecimalMax(value = "59")
    private Integer feeBasedOnTimeMinutes;

    @ApiModelProperty(value = "The service line total distance travelled claimed")
    @JsonProperty(value = "distance")
    @DecimalMax(value = "99999")
    private Integer feeBasedOnDistance;

    @ApiModelProperty(value = "The service line units claimed. Max = 9999999.99")
    @JsonProperty(value = "units")
    @DecimalMax(value = "9999999.99")
    private BigDecimal feeBasedOnUnits;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getFeeBasedOnTimeHours() {
        return feeBasedOnTimeHours;
    }

    public void setFeeBasedOnTimeHours(Integer feeBasedOnTimeHours) {
        this.feeBasedOnTimeHours = feeBasedOnTimeHours;
    }

    public Integer getFeeBasedOnTimeMinutes() {
        return feeBasedOnTimeMinutes;
    }

    public void setFeeBasedOnTimeMinutes(Integer feeBasedOnTimeMinutes) {
        this.feeBasedOnTimeMinutes = feeBasedOnTimeMinutes;
    }

    public Integer getFeeBasedOnDistance() {
        return feeBasedOnDistance;
    }

    public void setFeeBasedOnDistance(Integer feeBasedOnDistance) {
        this.feeBasedOnDistance = feeBasedOnDistance;
    }

    public BigDecimal getFeeBasedOnUnits() {
        return feeBasedOnUnits;
    }

    public void setFeeBasedOnUnits(BigDecimal feeBasedOnUnits) {
        this.feeBasedOnUnits = feeBasedOnUnits;
    }

    @Override
    public String toString() {
        return "ServiceCode{" +
                "code='" + code + '\'' +
                ", feeBasedOnTimeHours=" + feeBasedOnTimeHours +
                ", feeBasedOnTimeMinutes=" + feeBasedOnTimeMinutes +
                ", feeBasedOnDistance=" + feeBasedOnDistance +
                ", feeBasedOnUnits=" + feeBasedOnUnits +
                '}';
    }
}
