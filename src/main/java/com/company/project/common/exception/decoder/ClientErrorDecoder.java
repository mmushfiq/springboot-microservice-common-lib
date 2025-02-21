package com.company.project.common.exception.decoder;

import com.company.project.common.config.FeignConfig;
import com.company.project.common.exception.ClientException;
import com.company.project.common.exception.model.CommonErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;

@ConditionalOnBean(value = FeignConfig.class)
@Slf4j
public class ClientErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder;
    private final ObjectMapper objectMapper;

    public ClientErrorDecoder(ObjectMapper objectMapper) {
        this.defaultErrorDecoder = new Default();
        this.objectMapper = objectMapper;
    }

    @Override
    public Exception decode(String methodKey, Response response) {
        try (InputStream bodyIs = response.body().asInputStream()) {
            CommonErrorResponse error = objectMapper.readValue(bodyIs, CommonErrorResponse.class);
            String message = String.format("[%s] %s", methodKey, error.getMessage());
            return new ClientException(response.status(), error.getRequestId(), error.getErrorCode(), message,
                    error.getAdditionalFields());
        } catch (Exception ex) {
            log.error("Exception occurs while parsing client error response: {}", response, ex);
            return defaultErrorDecoder.decode(methodKey, response);
        }
    }

}
