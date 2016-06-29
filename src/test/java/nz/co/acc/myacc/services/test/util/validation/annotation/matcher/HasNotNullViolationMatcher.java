package nz.co.acc.myacc.services.test.util.validation.annotation.matcher;

import nz.co.acc.myacc.services.test.util.validation.annotation.ViolationType;
import org.hamcrest.Description;

/**
 * Validation matcher for identifying @NotBlank
 */
class HasNotNullViolationMatcher extends BaseHasAnnotationValidationMatcher {

    HasNotNullViolationMatcher(String fieldName) {
        super(fieldName, ViolationType.NOT_NULL);
    }

    public void describeTo(Description description) {
        description.appendValue("a not null violation for field '" + getExpectedField() + "'");
    }
}
