package com.company.project.common.util;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ConverterUtil {

    private static Map<String, String> countries = null;

    public static String convertToCountryIso2Code(String countryIso3Code) {
        if (countries == null) {
            String[] isoCountries = Locale.getISOCountries();
            countries = HashMap.newHashMap(isoCountries.length);
            for (String country : isoCountries) {
                Locale locale = Locale.of("", country);
                countries.put(locale.getISO3Country().toUpperCase(), locale.getCountry());
            }
        }
        return countries.get(countryIso3Code);
    }

}
