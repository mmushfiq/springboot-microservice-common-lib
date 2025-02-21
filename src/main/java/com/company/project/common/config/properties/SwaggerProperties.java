package com.company.project.common.config.properties;

import com.company.project.common.config.SwaggerConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnBean(value = SwaggerConfig.class)
@Configuration
@Getter
@Setter
public class SwaggerProperties {

    @Value("${application.swagger.info.title:Project Name}")
    private String title;

    @Value("${application.swagger.info.description:Spring Boot REST API for Your Project Name}")
    private String description;

    @Value("${application.swagger.info.version:1.0}")
    private String version;

    @Value("${application.swagger.info.termsOfServiceUrl:}")
    private String termsOfServiceUrl;

    @Value("${application.swagger.info.license:Apache License Version 2.0}")
    private String license;

    @Value("${application.swagger.info.licenseUrl:https://www.apache.org/licenses/LICENSE-2.0}")
    private String licenseUrl;

    @Value("${application.swagger.info.contact.name:Company, Your Squad Name}")
    private String contactName;

    @Value("${application.swagger.info.contact.url:https://company.com/}")
    private String contactUrl;

    @Value("${application.swagger.info.contact.email:info@company.com}")
    private String contactEmail;

    @Value("${application.swagger.info.server.domain:https://api-dev.company.com}")
    private String domainUrl;

    @Value("${application.swagger.info.server.baseUrl:/swagger-internal/}")
    private String baseUrl;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${server.port:8080}")
    private String port;

}
