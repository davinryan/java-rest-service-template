package com.davinvicky.service.test.util.validation.annotation.matcher;

import org.hamcrest.Matcher;

import java.util.Collection;

/**
 * Provides list of static imports to populate Annotation validation matchers.
 */
public class AnnotationValidationMatchers {

    public static Matcher<? super Collection> hasBlankFieldViolationForField(String fieldName) {
        return new HasNotBlankFieldViolationMatcher(fieldName);
    }

    public static Matcher<? super Collection> hasSizeViolationForField(String fieldName, Integer min, Integer max) {
        return new HasSizeFieldViolationMatcher(fieldName, min, max);
    }

    public static Matcher<? super Collection> hasPatternViolationForField(String fieldName, String pattern) {
        return new HasPatternFieldViolationMatcher(fieldName, pattern);
    }

    public static Matcher<? super Collection> hasNotNullViolationForField(String fieldName) {
        return new HasNotNullViolationMatcher(fieldName);
    }

    public static Matcher<? super Collection> hasDecimalMaxViolationForField(String fieldName, String max) {
        return new HasDecimalMaxFieldViolationMatcher(fieldName, max);
    }
}
