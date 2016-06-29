package nz.co.acc.myacc.services.invoicing.domain.request;

import nz.co.acc.myacc.services.invoicing.dao.DatabaseException;
import nz.co.acc.myacc.services.test.util.builders.CreateInvoiceFormBuilder;
import nz.co.acc.myacc.services.test.util.builders.ServiceCodeBuilder;
import nz.co.acc.myacc.services.test.util.builders.ServiceDetailsBuilder;
import org.hamcrest.MatcherAssert;
import org.hamcrest.core.Is;
import org.junit.Ignore;
import org.junit.Test;

import javax.jms.JMSException;
import javax.validation.ConstraintViolation;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static nz.co.acc.myacc.services.test.util.TextGenerator.generateAlphaText;
import static nz.co.acc.myacc.services.test.util.validation.annotation.AnnotationValidationUtils.testFieldCannotBeBlank;
import static nz.co.acc.myacc.services.test.util.validation.annotation.AnnotationValidationUtils.validate;
import static nz.co.acc.myacc.services.test.util.validation.annotation.ViolationType.*;
import static nz.co.acc.myacc.services.test.util.validation.annotation.matcher.AnnotationValidationMatchers.*;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Service Details Pojo.
 */
public class ServiceDetailsTest {

    @Test
    public void validateServiceDateTest() {
        // Setup
        ServiceDetails serviceDetails = new ServiceDetailsBuilder().createValid().build();
        String fieldName = "serviceDate";

        // Test valid
        Collection violations = validate(serviceDetails, fieldName);
        assertThat("Got " + violations.toString(), violations.size(), is(0));

        // Test invalid when null
        serviceDetails.setServiceDate(null);
        violations = validate(serviceDetails, NOT_NULL, fieldName);
        assertThat(violations, hasNotNullViolationForField(fieldName));
    }

    @Test
    public void validateProviderIdTest() {
        // Setup
        ServiceDetails serviceDetails = new ServiceDetailsBuilder().createValid().build();
        String fieldName = "providerId";

        // Test valid
        Collection violations = validate(serviceDetails, fieldName);
        assertThat("Got " + violations.toString(), violations.size(), is(0));

        // Test invalid maximum length
        serviceDetails.setProviderId(generateAlphaText(true, 9));
        violations = validate(serviceDetails, SIZE, fieldName);
        assertThat(violations, hasSizeViolationForField(fieldName, null, 8));

        // Test invalid pattern (all lower case)
        serviceDetails.setProviderId(generateAlphaText(false, 8));
        violations = validate(serviceDetails, PATTERN, fieldName);
        assertThat(violations, hasPatternViolationForField(fieldName, "[A-Z0-9]*"));
    }

    @Test
    public void validateServiceCodeTest() {
        // Setup
        ServiceCode serviceCode = new ServiceCodeBuilder().createValid().build();
        String fieldName = "code";

        // Test valid
        Collection violations = validate(serviceCode, fieldName);
        assertThat("Got " + violations.toString(), violations.size(), is(0));

        // Test invalid when blank
        testFieldCannotBeBlank(serviceCode, fieldName);

        // Test invalid minimum length
        serviceCode.setCode("");
        violations = validate(serviceCode, SIZE, fieldName);
        assertThat(violations, hasSizeViolationForField(fieldName, 1, 10));

        // Test invalid maximum length
        serviceCode.setCode(generateAlphaText(true, 11));
        violations = validate(serviceCode, SIZE, fieldName);
        assertThat(violations, hasSizeViolationForField(fieldName, 1, 10));

        // Test invalid pattern (all lower case)
        serviceCode.setCode(generateAlphaText(false, 10));
        violations = validate(serviceCode, PATTERN, fieldName);
        assertThat(violations, hasPatternViolationForField(fieldName, "[A-Z0-9]*"));
    }

    @Test
    public void validateFacilityCodeTest() {
        // Setup
        ServiceDetails serviceDetails = new ServiceDetailsBuilder().createValid().build();
        String fieldName = "facilityCode";

        // Test valid
        Collection violations = validate(serviceDetails, fieldName);
        assertThat("Got " + violations.toString(), violations.size(), is(0));

        // Test invalid maximum length
        serviceDetails.setFacilityCode(generateAlphaText(true, 7));
        violations = validate(serviceDetails, SIZE, fieldName);
        assertThat(violations, hasSizeViolationForField(fieldName, null, 6));
    }

    @Test
    public void validateServiceCommentsTest() {
        // Setup
        ServiceDetails serviceDetails = new ServiceDetailsBuilder().createValid().build();
        String fieldName = "serviceComments";

        // Test valid
        Collection violations = validate(serviceDetails, fieldName);
        assertThat("Got " + violations.toString(), violations.size(), is(0));

        // Test invalid maximum length
        serviceDetails.setServiceComments(generateAlphaText(true, 256));
        violations = validate(serviceDetails, SIZE, fieldName);
        assertThat(violations, hasSizeViolationForField(fieldName, null, 255));
    }

    @Test
    public void validateFeeTest() {
        // Setup
        ServiceDetails serviceDetails = new ServiceDetailsBuilder().createValid().build();
        String fieldName = "fee";

        // Test valid
        Collection violations = validate(serviceDetails, fieldName);
        assertThat("Got " + violations.toString(), violations.size(), is(0));

        // Test invalid when null
        serviceDetails.setFee(null);
        violations = validate(serviceDetails, NOT_NULL, fieldName);
        assertThat(violations, hasNotNullViolationForField(fieldName));

        // Test invalid maximum length
        serviceDetails.setFee(new BigDecimal(999999));
        violations = validate(serviceDetails, DECIMAL_MAX, fieldName);
        assertThat(violations, hasDecimalMaxViolationForField(fieldName, "99999.99"));
    }

    @Test
    public void validateFeeBasedAllNull() {
        // Setup
        ServiceCodeBuilder builder = new ServiceCodeBuilder();
        ServiceCode serviceCode = builder.createValid().build();

        // Test valid
        Collection violations = validate(serviceCode);
        assertThat("Got " + violations.toString(), violations.size(), is(0));

        // Test valid when all null
        serviceCode = builder.clearAllFeeBasedOn().build();
        violations = validate(serviceCode);
        assertThat("Got " + violations.toString(), violations.size(), is(0));
    }

    @Test
    public void validateFeeBasedOneNull() {
        // Setup
        ServiceCodeBuilder builder = new ServiceCodeBuilder();

        // Test invalid when no fee based on values
        ServiceCode serviceCode = builder.createValid().clearAllFeeBasedOn().build();
        serviceCode.setFeeBasedOnDistance(23);
        serviceCode.setFeeBasedOnUnits(new BigDecimal(24));
        Collection violations = validate(serviceCode);
        assertThat("Got " + violations.toString(), violations.size(), is(1));

        // Test invalid when two fee based on values
        serviceCode = builder.createValid().clearAllFeeBasedOn().build();
        serviceCode.setFeeBasedOnDistance(23);
        serviceCode.setFeeBasedOnTimeHours(23);
        serviceCode.setFeeBasedOnTimeMinutes(35);
        violations = validate(serviceCode);
        assertThat("Got " + violations.toString(), violations.size(), is(1));

        // Test invalid when two fee based on values
        serviceCode = builder.createValid().clearAllFeeBasedOn().build();
        serviceCode.setFeeBasedOnUnits(new BigDecimal(23));
        serviceCode.setFeeBasedOnDistance(25);
        violations = validate(serviceCode);
        assertThat("Got " + violations.toString(), violations.size(), is(1));

        // Test invalid when two fee based on values
        serviceCode = builder.createValid().clearAllFeeBasedOn().build();
        serviceCode.setFeeBasedOnUnits(new BigDecimal(23));
        serviceCode.setFeeBasedOnTimeHours(23);
        serviceCode.setFeeBasedOnTimeMinutes(35);
        violations = validate(serviceCode);
        assertThat("Got " + violations.toString(), violations.size(), is(1));
    }

    @Test
    public void testPrepopulateTotalMinutesAndHoursForMFP() {
        // Setup Invoice with two service codes whose minutes total 60 minutes.
        ServiceDetailsBuilder detailsBuilder = new ServiceDetailsBuilder();
        detailsBuilder.createServiceCodeWithFeeBasedOnMinutes(35);
        detailsBuilder.createServiceCodeWithFeeBasedOnMinutes(35);
        detailsBuilder.createServiceCodeWithFeeBasedOnHours(12);

        // Test
        ServiceDetails details = detailsBuilder.build();

        // Verify That minutes are reset to zero and hours bumped up to 1
        assertThat(details.getTotalFeeBasedOnTimeHours(), is(13));
        assertThat(details.getTotalFeeBasedOnTimeMinutes(), is(10));

    }

    @Test
    public void validateServiceCodesHoursNotGreaterThanMax() {
        // Setup
        ServiceDetailsBuilder detailsBuilder = new ServiceDetailsBuilder();
        ServiceDetails serviceDetails = detailsBuilder.createValid().build();
        ServiceCodeBuilder codeBuilder = new ServiceCodeBuilder();
        ServiceCode serviceCode = codeBuilder.createServiceCodeFeeBasedOnMinutes(30).build();
        ServiceCode serviceCode2 = codeBuilder.createServiceCodeFeeBasedOnMinutes(30).build();
        List<ServiceCode> serviceCodes = new ArrayList<ServiceCode>();
        serviceCodes.add(serviceCode);
        serviceCodes.add(serviceCode2);
        serviceDetails.setServiceCodes(serviceCodes);

        // Test valid
        Collection violations = validate(serviceDetails);
        assertThat("Got " + violations.toString(), violations.size(), is(1));
        ConstraintViolation violation = (ConstraintViolation)violations.iterator().next();
        assertThat(violation.getMessage(), is("SumOfTimeFieldsShouldNotExceedValue validation failed on this object. Look at @SumOfTimeFieldsShouldNotExceedValue annotation at top of validating class for more details."));
    }
}
