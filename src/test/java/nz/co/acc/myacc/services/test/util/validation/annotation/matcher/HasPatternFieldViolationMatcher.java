package nz.co.acc.myacc.services.test.util.validation.annotation.matcher;

import nz.co.acc.myacc.services.test.util.validation.annotation.ViolationType;
import org.hamcrest.Description;

import javax.validation.ConstraintViolation;
import java.util.List;
import java.util.Map;

/**
 * Validation matcher for identifying @NotBlank
 */
class HasPatternFieldViolationMatcher extends BaseHasAnnotationValidationMatcher {

    private final String pattern;

    HasPatternFieldViolationMatcher(String fieldName, String pattern) {
        super(fieldName, ViolationType.PATTERN);
        this.pattern = pattern;
    }

    public void describeTo(Description description) {
        description.appendValue("a pattern violation for field '" + getExpectedField() + "'");
    }

    @Override
    public boolean matches(Object item) {
        if (item != null && item instanceof List) {
            List<Object> list = (List) item;
            for (Object listItem : list) {
                if (listItem != null && listItem instanceof ConstraintViolation) {
                    ConstraintViolation violation = (ConstraintViolation) listItem;
                    Map violationAttributes = violation.getConstraintDescriptor().getAttributes();
                    if (violation.getPropertyPath().toString().equalsIgnoreCase(getExpectedField())
                            && getViolationType().appliesTo(violation)
                            && patternMatches(violationAttributes)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean patternMatches(Map violationAttributes) {
        return pattern != null && pattern.equals(violationAttributes.get("regexp"));
    }
}
