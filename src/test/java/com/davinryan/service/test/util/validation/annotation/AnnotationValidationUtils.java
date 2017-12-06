package com.davinryan.service.test.util.validation.annotation;

import org.hibernate.validator.HibernateValidator;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import javax.validation.ConstraintViolation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.davinryan.service.test.util.validation.annotation.matcher.AnnotationValidationMatchers.hasBlankFieldViolationForField;
import static org.junit.Assert.assertThat;

/**
 * Useful utility methods for validating Annotation validation.
 */
public class AnnotationValidationUtils {

    private static final LocalValidatorFactoryBean localValidatorFactory;

    static {
        localValidatorFactory = new LocalValidatorFactoryBean();
        localValidatorFactory.setProviderClass(HibernateValidator.class);
        localValidatorFactory.afterPropertiesSet();
    }

    /**
     * Tests that field validates correctly when field set to null, empty string and space.
     * @param obj object to validate
     * @param fieldName field to test validation for
     */
    public static void testFieldCannotBeBlank(Object obj, String fieldName) {
        // Test invalid when null
        ReflectionTestUtils.invokeSetterMethod(obj, fieldName, null);
        assertBlankFieldViolation(obj, fieldName);

        // Test invalid when empty
        ReflectionTestUtils.invokeSetterMethod(obj, fieldName, "");
        assertBlankFieldViolation(obj, fieldName);

        // Test invalid when space
        ReflectionTestUtils.invokeSetterMethod(obj, fieldName, " ");
        assertBlankFieldViolation(obj, fieldName);
    }

    public static <T> void assertBlankFieldViolation(T request, String fieldName) {
        Collection violations;
        violations = validate(request, fieldName);
        assertThat(violations, hasBlankFieldViolationForField(fieldName));
    }

    /**
     * Validates using hibernate validator.
     *
     * @param obj           object to validate
     * @return
     */
    public static <T> Collection<ConstraintViolation<T>> validate(T obj) {
        return localValidatorFactory.validate(obj);
    }

    /**
     * Validates using hibernate validator. Returns all violations that match given violation type.
     *
     * @param obj           object to validate
     * @param violationType violation types to filter on
     * @return
     */
    public static <T> Collection<ConstraintViolation<T>> validate(T obj, ViolationType violationType) {
        return filterViolationsOnViolationType(localValidatorFactory.validate(obj), violationType);
    }

    /**
     * Validates using hibernate validator. Returns all violations that match given violation type for a specific field.
     *
     * @param obj           object to validate
     * @param violationType violation types to filter on
     * @param fieldName     field to filter on
     * @return
     */
    public static <T> Collection<ConstraintViolation<T>> validate(T obj, ViolationType violationType, String fieldName) {
        return filterViolationsOnFieldName(filterViolationsOnViolationType(localValidatorFactory.validate(obj), violationType), fieldName);
    }

    /**
     * Validates using hibernate validator. Returns all violations for a specific field.
     *
     * @param obj       object to validate
     * @param fieldName field to filter on
     * @return
     */
    public static <T> Collection<ConstraintViolation<T>> validate(T obj, String fieldName) {
        return filterViolationsOnFieldName(localValidatorFactory.validate(obj), fieldName);
    }

    private static <T> Collection<ConstraintViolation<T>> filterViolationsOnFieldName(Collection<ConstraintViolation<T>> violations, String fieldName) {
        List filteredList = new ArrayList();
        for (ConstraintViolation violation : violations) {
            if (violation.getPropertyPath().toString().equalsIgnoreCase(fieldName)) {
                filteredList.add(violation);
            }
        }
        return filteredList;
    }

    private static <T> Collection<ConstraintViolation<T>> filterViolationsOnViolationType(Collection<ConstraintViolation<T>> violations, ViolationType violationType) {
        List filteredList = new ArrayList();
        for (ConstraintViolation violation : violations) {
            if (violationType.appliesTo(violation)) {
                filteredList.add(violation);
            }
        }
        return filteredList;
    }
}
