package com.davinvicky.service.domain.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import com.davinvicky.common.logging.RedactWhenLogging;
import com.davinvicky.common.validation.Field;
import com.davinvicky.service.validation.SumOfDistanceShouldNotExceedValue;
import com.davinvicky.common.validation.SumOfTimeFieldsShouldNotExceedValue;
import com.davinvicky.service.validation.SumOfUnitsShouldNotExceedValue;
import org.joda.time.DateTime;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@ApiModel(description = "Main request object for submitting a single schedule/service line.")
@SumOfTimeFieldsShouldNotExceedValue(
        max = "59",
        maxUnits = "minutes",
        fields = {
                @Field(name = "serviceCodes", subFieldName = "feeBasedOnTimeHours", units = "hours"),
                @Field(name = "serviceCodes", subFieldName = "feeBasedOnTimeMinutes", units = "minutes")
        }
)

/*
 * One day when we use java 1.7 and above we can use {@link java.lang.annotation.Repeatable} instead
 * of having two validation annotations like the ones below point to the same validator.
 */
@SumOfDistanceShouldNotExceedValue(
        max = "99999",
        fields = {@Field(name = "serviceCodes", subFieldName = "feeBasedOnDistance")}
)

@SumOfUnitsShouldNotExceedValue(
        max = "999.99",
        fields = {@Field(name = "serviceCodes", subFieldName = "feeBasedOnUnits")}
)
public class ServiceDetails {

    @ApiModelProperty(value = "The standard ACC identifier of the provider. Format = [A-Z0-9]*. Max = 8")
    @Size(max = 8)
    @Pattern(regexp = "[A-Z0-9]*")
    private String providerId;

    @ApiModelProperty(value = "The date the provider performed the treatment. Format = yyyy-MM-dd'T'HH:mm:ss.SSSZ", required = true)
    @NotNull
    private DateTime serviceDate;

    @ApiModelProperty(value = "The facility code associated to the service line  Format = A5. Max = 6")
    @Size(max = 6)
    private String facilityCode;

    @Valid
    private List<ServiceCode> serviceCodes = new ArrayList<ServiceCode>();

    @ApiModelProperty(value = "A free form service line comment. Max = 255")
    @Size(max = 255)
    @RedactWhenLogging
    private String serviceComments;

    @ApiModelProperty(value = "The service line total amount claimed. The total amount claimed must be positive as credits are not allowed. Max = 99999.99", required = true)
    @NotNull
    @DecimalMax(value = "99999.99")
    private BigDecimal fee;

    /**
     * Purely used for getting the total minutes from all service codes and formatting specially for MFP. MFP can only
     * handle 2 digits for minutes so when we hit 60 we need to bump hours up by 1.
     */
    private Integer totalFeeBasedOnTimeMinutes = 0;

    /**
     * Purely used for getting the total hours from all service codes and formatting specially for MFP.
     */
    private Integer totalFeeBasedOnTimeHours = 0;

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public DateTime getServiceDate() {
        return serviceDate;
    }

    public void setServiceDate(DateTime serviceDate) {
        this.serviceDate = serviceDate;
    }

    public String getFacilityCode() {
        return facilityCode;
    }

    public void setFacilityCode(String facilityCode) {
        this.facilityCode = facilityCode;
    }

    public List<ServiceCode> getServiceCodes() {
        return serviceCodes;
    }

    public void setServiceCodes(List<ServiceCode> serviceCodes) {
        this.serviceCodes = serviceCodes;
    }

    public String getServiceComments() {
        return serviceComments;
    }

    public void setServiceComments(String serviceComments) {
        this.serviceComments = serviceComments;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public void setFee(BigDecimal fee) {
        this.fee = fee;
    }

    public Integer getTotalFeeBasedOnTimeMinutes() {
        // handle minutes and hours. It appears that MFP can only handle 2 digits for minutes and 2 digits for hours.
        // So you guessed it we need to fill up hours when minutes is too high
        updateTotalMinutesAndHoursForServiceDetail();
        return totalFeeBasedOnTimeMinutes;
    }

    public Integer getTotalFeeBasedOnTimeHours() {
        // handle minutes and hours. It appears that MFP can only handle 2 digits for minutes and 2 digits for hours.
        // So you guessed it we need to fill up hours when minutes is too high
        updateTotalMinutesAndHoursForServiceDetail();
        return totalFeeBasedOnTimeHours;
    }

    private void updateTotalMinutesAndHoursForServiceDetail() {
        Integer totalMinutes = 0;
        Integer totalHours = 0;
        if (getServiceCodes() != null) {
            for (ServiceCode code : getServiceCodes()) {
                if (code.getFeeBasedOnTimeMinutes() != null) {
                    totalMinutes += code.getFeeBasedOnTimeMinutes();
                }
                if (code.getFeeBasedOnTimeHours() != null) {
                    totalHours += code.getFeeBasedOnTimeHours();
                }
            }
        }
        int extraHours = totalMinutes / 60;
        totalFeeBasedOnTimeHours = totalHours + extraHours;
        int remainingMinutes = totalMinutes - (extraHours * 60);
        totalFeeBasedOnTimeMinutes = remainingMinutes;
    }

    @Override
    public String toString() {
        return "ServiceDetails{" +
                "providerId='" + providerId + '\'' +
                ", serviceDate=" + serviceDate +
                ", facilityCode='" + facilityCode + '\'' +
                ", serviceCodes=" + serviceCodes +
                ", serviceComments='" + serviceComments + '\'' +
                ", fee=" + fee +
                ", totalFeeBasedOnTimeMinutes=" + totalFeeBasedOnTimeMinutes +
                ", totalFeeBasedOnTimeHours=" + totalFeeBasedOnTimeHours +
                '}';
    }
}
