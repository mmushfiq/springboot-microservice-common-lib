package com.company.project.common.util;

import com.company.project.common.config.properties.SwaggerProperties;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SwaggerUtil {

    public static Info convertToSpringDocApiInfo(SwaggerProperties props) {
        return new Info()
                .title(props.getTitle())
                .description(props.getDescription())
                .version(props.getVersion())
                .contact(contact(props))
                .termsOfService(props.getTermsOfServiceUrl())
                .license(license(props));

    }

    private static Contact contact(SwaggerProperties props) {
        return new Contact()
                .name(props.getContactName())
                .email(props.getContactEmail())
                .url(props.getContactUrl());
    }

    private static License license(SwaggerProperties props) {
        return new License()
                .name(props.getLicense())
                .url(props.getLicenseUrl());
    }

}
