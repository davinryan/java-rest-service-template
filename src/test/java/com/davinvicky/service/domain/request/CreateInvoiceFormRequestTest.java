package com.davinvicky.service.domain.request;

import com.davinvicky.service.test.util.builders.CreateInvoiceFormBuilder;
import org.junit.Test;

import java.util.Collection;

import static com.davinvicky.service.test.util.TextGenerator.generateAlphaText;
import static com.davinvicky.service.test.util.validation.annotation.AnnotationValidationUtils.testFieldCannotBeBlank;
import static com.davinvicky.service.test.util.validation.annotation.AnnotationValidationUtils.validate;
import static com.davinvicky.service.test.util.validation.annotation.ViolationType.*;
import static com.davinvicky.service.test.util.validation.annotation.matcher.AnnotationValidationMatchers.hasNotNullViolationForField;
import static com.davinvicky.service.test.util.validation.annotation.matcher.AnnotationValidationMatchers.hasPatternViolationForField;
import static com.davinvicky.service.test.util.validation.annotation.matcher.AnnotationValidationMatchers.hasSizeViolationForField;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Tests validation of {@link CreateInvoiceFormRequest}.
 */
public class CreateInvoiceFormRequestTest {

    @Test
    public void validateCorrelationIdTest() {
        // Setup
        CreateInvoiceFormRequest request = new CreateInvoiceFormBuilder().createValid().build();
        String fieldName = "correlationId";

        // Test valid
        Collection violations = validate(request, fieldName);
        assertThat("Got " + violations.toString(), violations.size(), is(0));

        // Test invalid when blank
        testFieldCannotBeBlank(request, fieldName);
    }

    @Test
    public void validateTypeTest() {
        // Setup
        CreateInvoiceFormRequest request = new CreateInvoiceFormBuilder().createValid().build();
        String fieldName = "type";

        // Test valid
        Collection violations = validate(request, fieldName);
        assertThat("Got " + violations.toString(), violations.size(), is(0));

        // Test invalid when blank
        testFieldCannotBeBlank(request, fieldName);
    }

    @Test
    public void validateVendorIdTest() {
        // Setup
        CreateInvoiceFormRequest request = new CreateInvoiceFormBuilder().createValid().build();
        String fieldName = "vendorId";

        // Test valid
        Collection violations = validate(request, fieldName);
        assertThat("Got " + violations.toString(), violations.size(), is(0));

        // Test invalid when blank
        testFieldCannotBeBlank(request, fieldName);

        // Test invalid minimum length
        request.setVendorId("");
        violations = validate(request, SIZE, fieldName);
        assertThat(violations, hasSizeViolationForField(fieldName, 1, 12));

        // Test invalid maximum length
        request.setVendorId(generateAlphaText(true, 13));
        violations = validate(request, SIZE, fieldName);
        assertThat(violations, hasSizeViolationForField(fieldName, 1, 12));

        // Test invalid pattern (all lower case)
        request.setVendorId(generateAlphaText(false, 12));
        violations = validate(request, PATTERN, fieldName);
        assertThat(violations, hasPatternViolationForField(fieldName, "[^a-z]*"));
    }

    @Test
    public void validateContractIdTest() {
        // Setup
        CreateInvoiceFormRequest request = new CreateInvoiceFormBuilder().createValid().build();
        String fieldName = "contractNumber";

        // Test valid
        Collection violations = validate(request, fieldName);
        assertThat("Got " + violations.toString(), violations.size(), is(0));

        // Test invalid maximum length
        request.setContractNumber(generateAlphaText(true, 9));
        violations = validate(request, SIZE, fieldName);
        assertThat(violations, hasSizeViolationForField(fieldName, null, 8));

        // Test invalid pattern (all lower case)
        request.setContractNumber(generateAlphaText(false, 8));
        violations = validate(request, PATTERN, fieldName);
        assertThat(violations, hasPatternViolationForField(fieldName, "[^a-z]*"));
    }

    @Test
    public void validateFirstNameTest() {
        // Setup
        CreateInvoiceFormRequest request = new CreateInvoiceFormBuilder().createValid().build();
        String fieldName = "firstName";

        // Test valid
        Collection violations = validate(request,  fieldName);
        assertThat("Got " + violations.toString(), violations.size(), is(0));

        // Test invalid when blank
        testFieldCannotBeBlank(request, fieldName);

        // Test invalid minimum length
        request.setFirstName("");
        violations = validate(request, SIZE, fieldName);
        assertThat(violations, hasSizeViolationForField(fieldName, 1, 20));

        // Test invalid maximum length
        request.setFirstName(generateAlphaText(true, 21));
        violations = validate(request, SIZE, fieldName);
        assertThat(violations, hasSizeViolationForField(fieldName, 1, 20));
    }

    @Test
    public void validateLastNameTest() {
        // Setup
        CreateInvoiceFormRequest request = new CreateInvoiceFormBuilder().createValid().build();
        String fieldName = "surname";

        // Test valid
        Collection violations = validate(request,  fieldName);
        assertThat("Got " + violations.toString(), violations.size(), is(0));

        // Test invalid when blank
        testFieldCannotBeBlank(request, fieldName);

        // Test invalid minimum length
        request.setSurname("");
        violations = validate(request, SIZE, fieldName);
        assertThat(violations, hasSizeViolationForField(fieldName, 1, 25));

        // Test invalid maximum length
        request.setSurname(generateAlphaText(true, 26));
        violations = validate(request, SIZE, fieldName);
        assertThat(violations, hasSizeViolationForField(fieldName, 1, 25));
    }

    @Test
    public void validateDOBTest() {
        // Setup
        CreateInvoiceFormRequest request = new CreateInvoiceFormBuilder().createValid().build();
        String fieldName = "dob";

        // Test valid
        Collection violations = validate(request,  fieldName);
        assertThat("Got " + violations.toString(), violations.size(), is(0));

        // Test invalid when null
        request.setDob(null);
        violations = validate(request, NOT_NULL, fieldName);
        assertThat(violations, hasNotNullViolationForField(fieldName));
    }

    @Test
    public void validateNHITest() {
        // Setup
        CreateInvoiceFormRequest request = new CreateInvoiceFormBuilder().createValid().build();
        String fieldName = "nhi";

        // Test valid
        Collection violations = validate(request,  fieldName);
        assertThat("Got " + violations.toString(), violations.size(), is(0));

        // Test invalid maximum length
        request.setNhi(generateAlphaText(true, 8));
        violations = validate(request, SIZE, fieldName);
        assertThat(violations, hasSizeViolationForField(fieldName, 0, 7));
    }

    @Test
    public void validateClaimNumberTest() {
        // Setup
        CreateInvoiceFormRequest request = new CreateInvoiceFormBuilder().createValid().build();
        String fieldName = "claimNumber";

        // Test valid
        Collection violations = validate(request,  fieldName);
        assertThat("Got " + violations.toString(), violations.size(), is(0));

        // Test invalid when blank
        testFieldCannotBeBlank(request, fieldName);

        // Test invalid minimum length
        request.setClaimNumber("");
        violations = validate(request, SIZE, fieldName);
        assertThat(violations, hasSizeViolationForField(fieldName, 1, 12));

        // Test invalid maximum length
        request.setClaimNumber(generateAlphaText(true, 13));
        violations = validate(request, SIZE, fieldName);
        assertThat(violations, hasSizeViolationForField(fieldName, 1, 12));

        // Test invalid pattern (all lower case)
        request.setClaimNumber(generateAlphaText(false, 8));
        violations = validate(request, PATTERN, fieldName);
        assertThat(violations, hasPatternViolationForField(fieldName, "[A-Z0-9]*"));
    }

    @Test
    public void validateAdditionalCommentsTest() {
        // Setup
        CreateInvoiceFormRequest request = new CreateInvoiceFormBuilder().createValid().build();
        String fieldName = "additionalComments";

        // Test valid
        Collection violations = validate(request,  fieldName);
        assertThat("Got " + violations.toString(), violations.size(), is(0));

        // Test invalid maximum length
        request.setAdditionalComments(generateAlphaText(true, 256));
        violations = validate(request, SIZE, fieldName);
        assertThat(violations, hasSizeViolationForField(fieldName, 0, 255));
    }

    @Test
    public void validateInvoiceNumberTest() {
        // Setup
        CreateInvoiceFormRequest request = new CreateInvoiceFormBuilder().createValid().build();
        String fieldName = "invoiceNumber";

        // Test valid
        Collection violations = validate(request,  fieldName);
        assertThat("Got " + violations.toString(), violations.size(), is(0));

        // Test invalid maximum length
        request.setInvoiceNumber(generateAlphaText(true, 11));
        violations = validate(request, SIZE, fieldName);
        assertThat(violations, hasSizeViolationForField(fieldName, null, 10));
    }

    @Test
    public void validateInvoiceDateTest() {
        // Setup
        CreateInvoiceFormRequest request = new CreateInvoiceFormBuilder().createValid().build();
        String fieldName = "invoiceDate";

        // Test valid
        Collection violations = validate(request,  fieldName);
        assertThat("Got " + violations.toString(), violations.size(), is(0));

        // Test invalid when null
        request.setInvoiceDate(null);
        violations = validate(request, NOT_NULL, fieldName);
        assertThat(violations, hasNotNullViolationForField(fieldName));
    }

}
