package com.davinvicky.service.test.util.validation.annotation.matcher;

import com.davinvicky.service.test.util.validation.annotation.ViolationType;
import org.hamcrest.Description;

/**
 * Validation matcher for identifying @NotBlank
 */
class HasNotBlankFieldViolationMatcher extends BaseHasAnnotationValidationMatcher {

    HasNotBlankFieldViolationMatcher(String fieldName) {
        super(fieldName, ViolationType.NOT_BLANK);
    }

    public void describeTo(Description description) {
        description.appendValue("a not blank violation for field '" + getExpectedField() + "'");
    }
}
