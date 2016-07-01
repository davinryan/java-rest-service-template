package com.davinvicky.service.test.util.validation.annotation.matcher;

import com.davinvicky.service.test.util.validation.annotation.ViolationType;
import org.hamcrest.Description;

import javax.validation.ConstraintViolation;
import java.util.List;
import java.util.Map;

/**
 * Validation matcher for identifying @Size(min = toSomething).
 */
class HasDecimalMaxFieldViolationMatcher extends BaseHasAnnotationValidationMatcher {

    private final String max;

    HasDecimalMaxFieldViolationMatcher(String fieldName, String max) {
        super(fieldName, ViolationType.DECIMAL_MAX);
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
                            && maxAttributeMatches(violationAttributes)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean maxAttributeMatches(Map violationAttributes) {
        if (this.max != null) {
            return this.max.equals(violationAttributes.get("value"));
        } else {
            return violationAttributes.get("max") == null;
        }
    }

    public void describeTo(Description description) {
        description.appendValue("a DecimalMax violation for field '" + getExpectedField() + "'");
    }
}
