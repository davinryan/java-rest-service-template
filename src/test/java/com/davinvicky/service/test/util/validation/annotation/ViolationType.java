package com.davinvicky.service.test.util.validation.annotation;

import org.hibernate.validator.constraints.NotBlank;

import javax.validation.ConstraintViolation;
import javax.validation.constraints.*;
import java.lang.annotation.Annotation;

/**
 * Created by ryanda on 06/05/2016.
 */
public enum ViolationType {
    NOT_BLANK(NotBlank.class),
    SIZE(Size.class),
    PATTERN(Pattern.class),
    NOT_NULL(NotNull.class),
    DECIMAL_MAX(DecimalMax.class);

    private Class<? extends Annotation> validationAnnoation;

    ViolationType(Class<? extends Annotation> validationAnnoation) {
        this.validationAnnoation = validationAnnoation;
    }

    public Class<? extends Annotation> getValidationAnnoation() {
        return validationAnnoation;
    }

    public boolean appliesTo(ConstraintViolation violation) {
        Class<? extends Annotation> annotationType = violation.getConstraintDescriptor().getAnnotation().annotationType();
        return annotationType.equals(this.validationAnnoation);
    }
}
