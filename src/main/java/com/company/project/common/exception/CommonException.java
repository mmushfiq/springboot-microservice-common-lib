package com.company.project.common.exception;

import java.util.Map;
import lombok.Getter;

@Getter
public class CommonException extends RuntimeException {

    private final String errorCode;
    private final String message;
    private final Map<String, Object> additionalFields;

    protected CommonException(String errorCode, String message) {
        this(errorCode, message, null);
    }

    protected CommonException(String errorCode, String message, Map<String, Object> additionalFields) {
        super(errorCode);
        this.errorCode = errorCode;
        this.message = message;
        this.additionalFields = additionalFields;
    }

}
