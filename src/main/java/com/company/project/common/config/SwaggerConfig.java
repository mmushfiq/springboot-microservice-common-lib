package com.company.project.common.config;

import com.company.project.common.config.properties.SwaggerProperties;
import com.company.project.common.util.SwaggerUtil;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.SpecVersion;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StopWatch;

@ConditionalOnProperty(prefix = "common.swagger", name = "enabled", matchIfMissing = true)
@OpenAPIDefinition
@Configuration
@RequiredArgsConstructor
@Slf4j
public class SwaggerConfig {

    private final SwaggerProperties properties;

    @Bean
    public OpenAPI openApi() {
        log.debug("Starting Swagger");
        StopWatch watch = new StopWatch();
        watch.start();
        OpenAPI info = new OpenAPI()
                .info(SwaggerUtil.convertToSpringDocApiInfo(properties))
                .specVersion(SpecVersion.V31)
                .servers(createServer(properties));

        watch.stop();
        log.debug("Started Swagger in {} ms", watch.getTotalTimeMillis());
        return info;

    }

    private List<Server> createServer(SwaggerProperties properties) {
        return List.of(
                new Server().url(properties.getDomainUrl() + properties.getBaseUrl() + properties.getApplicationName()),
                new Server().url("http://localhost" + ":" + properties.getPort())
        );
    }

}
