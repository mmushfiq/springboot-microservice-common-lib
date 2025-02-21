package com.company.project.common.exception;

import java.util.Map;
import lombok.Getter;

@Getter
public class ClientException extends RuntimeException {

    private final int status;
    private final String requestId;
    private final String errorCode;
    private final String message;
    private final Map<String, Object> additionalFields;

    public ClientException(int status, String requestId, String errorCode,
                           String message, Map<String, Object> additionalFields) {
        super(errorCode);
        this.status = status;
        this.requestId = requestId;
        this.errorCode = errorCode;
        this.message = message;
        this.additionalFields = additionalFields;
    }

}
