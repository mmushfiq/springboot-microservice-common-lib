package com.company.project.common.exception.validation.constraint;

import com.company.project.common.exception.validation.CountryValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Locale;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CountryValidator.class)
@Documented
public @interface Country {

    String message() default "should be in ISO format";

    Locale.IsoCountryCode isoCode() default Locale.IsoCountryCode.PART1_ALPHA3;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
