package com.company.project.common.exception.validation;

import static java.util.Locale.getISOCountries;

import com.company.project.common.exception.validation.constraint.Country;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Locale.IsoCountryCode;
import java.util.Set;

public class CountryValidator implements ConstraintValidator<Country, String> {

    private IsoCountryCode isoCode;
    private Set<String> countryList = new HashSet<>();

    @Override
    public void initialize(Country constraintAnnotation) {
        isoCode = constraintAnnotation.isoCode();
        countryList = isoCountryList(isoCode);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }

        boolean isValid = countryList.contains(value);

        if (!isValid) {
            String message;
            if (isoCode == IsoCountryCode.PART1_ALPHA2) {
                message = "should be in the ISO3166-1 alpha-2 two letter country code format";
            } else if (isoCode == IsoCountryCode.PART1_ALPHA3) {
                message = "should be in the ISO3166-1 alpha-3 three letter country code format";
            } else if (isoCode == IsoCountryCode.PART3) {
                message = "should be in the ISO3166-3 four letter country code format";
            } else {
                message = "should be in ISO format";
            }
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
        }

        return isValid;
    }

    private Set<String> isoCountryList(IsoCountryCode isoCode) {
        return getISOCountries(isoCode);
    }

}
