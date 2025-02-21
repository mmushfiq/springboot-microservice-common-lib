package com.company.project.common.config;

import static com.company.project.common.model.constant.CommonConstants.HttpHeader.PN_ORIGIN_SERVICE;

import com.company.project.common.exception.decoder.ClientErrorDecoder;
import com.company.project.common.util.WebUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import feign.okhttp.OkHttpClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(prefix = "common.openfeign", name = "enabled", matchIfMissing = true)
@Configuration
@EnableFeignClients(basePackages = "com.company.project")
@RequiredArgsConstructor
public class FeignConfig {

    @Value("${spring.application.name}")
    private String serviceName;

    private final WebUtil webUtil;

    @Bean
    public ErrorDecoder errorDecoder(ObjectMapper objectMapper) {
        return new ClientErrorDecoder(objectMapper);
    }

    @Bean
    public OkHttpClient client() {
        return new OkHttpClient();
    }

    @Bean
    public RequestInterceptor feignRequestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header(PN_ORIGIN_SERVICE, serviceName);
            webUtil.getProjectBasedHeaders().forEach((headerName, headerValue) -> {
                if (!requestTemplate.headers().containsKey(headerName)) {
                    requestTemplate.header(headerName, headerValue);
                }
            });
        };
    }

}
