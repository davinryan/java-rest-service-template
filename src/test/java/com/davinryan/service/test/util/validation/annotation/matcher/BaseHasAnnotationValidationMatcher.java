package com.davinryan.service.test.util.validation.annotation.matcher;

import com.davinryan.service.test.util.validation.annotation.ViolationType;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

import javax.validation.ConstraintViolation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by ryanda on 06/05/2016.
 */
abstract class BaseHasAnnotationValidationMatcher extends BaseMatcher<Collection> {

    private final String expectedField;

    private final ViolationType violationType;

    BaseHasAnnotationValidationMatcher(String fieldName, ViolationType violationType) {
        this.expectedField = fieldName;
        this.violationType = violationType;
    }

    @Override
    public void describeMismatch(Object item, Description description) {
        description.appendValue("instead got violation list containing: " + getViolations(item));
    }

    public boolean matches(Object item) {
        if (item != null && item instanceof List) {
            List<Object> list = (List) item;
            for (Object listItem : list) {
                if (listItem != null && listItem instanceof ConstraintViolation) {
                    ConstraintViolation violation = (ConstraintViolation) listItem;
                    if (violation.getPropertyPath().toString().equalsIgnoreCase(getExpectedField()) && violationType.appliesTo(violation)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected List<String> getViolations(Object item) {
        List<String> violations = new ArrayList();
        if (item != null && item instanceof List) {
            List<Object> list = (List)item;
            for(Object listItem : list) {
                if (listItem != null && listItem instanceof ConstraintViolation) {
                    ConstraintViolation violation = (ConstraintViolation)listItem;
                    violations.add(violation.toString());
                }
            }
        }
        return violations;
    }

    protected String getExpectedField() {
        return expectedField;
    }

    protected ViolationType getViolationType() {
        return violationType;
    }
}
