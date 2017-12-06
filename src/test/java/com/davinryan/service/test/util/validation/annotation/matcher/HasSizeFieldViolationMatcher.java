package com.davinryan.service.test.util.validation.annotation.matcher;

import com.davinryan.service.test.util.validation.annotation.ViolationType;
import org.hamcrest.Description;

import javax.validation.ConstraintViolation;
import java.util.List;
import java.util.Map;

/**
 * Validation matcher for identifying @Size(min = toSomething).
 */
class HasSizeFieldViolationMatcher extends BaseHasAnnotationValidationMatcher {

    private final Integer min;
    private final Integer max;

    HasSizeFieldViolationMatcher(String fieldName, Integer min, Integer max) {
        super(fieldName, ViolationType.SIZE);
        this.min = min;
        this.max = max;
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
                            && minOrAndMaxMatch(violationAttributes)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean minOrAndMaxMatch(Map violationAttributes) {
        if (this.min != null && this.max != null) {
            return this.min.equals(violationAttributes.get("min")) && this.max.equals(violationAttributes.get("max"));
        } else if (this.min != null && this.max == null) {
            return this.min.equals(violationAttributes.get("min"));
        } else if (this.min == null && this.max != null) {
            return this.max.equals(violationAttributes.get("max"));
        }

        // Return true because min and max are both null so they do match.
        return true;
    }

    public void describeTo(Description description) {
        description.appendValue("a size violation for field '" + getExpectedField() + "'");
    }
}
