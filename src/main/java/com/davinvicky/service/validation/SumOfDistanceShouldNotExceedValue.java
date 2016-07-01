package com.davinvicky.service.validation;

import com.davinvicky.common.validation.Field;
import com.davinvicky.common.validation.SumOfFieldsShouldNotExceedValueValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Validates that Sum of fields specified by {@link Field} do not exceed a specified maximum value.
 */
@Target({TYPE, ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = SumOfFieldsShouldNotExceedValueValidator.class)
@Documented
public @interface SumOfDistanceShouldNotExceedValue {
    String message() default "SumOfDistanceShouldNotExceedValue validation failed on this object. Look at @SumOfDistanceShouldNotExceedValue annotation at top of class for more details.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * Maximum value of the sum of the values from fields.
     *
     * @return
     */
    String max() default "0";

    /**
     * Fields to get values from to do the calculation.
     *
     * @return
     */
    Field[] fields() default {};

    /**
     * Defines several <code>@FieldMatch</code> annotations on the same element.
     *
     * @see SumOfDistanceShouldNotExceedValue
     */
    @Target({TYPE, ANNOTATION_TYPE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        SumOfDistanceShouldNotExceedValue[] value();
    }
}
