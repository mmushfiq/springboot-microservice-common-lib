package com.company.project.common.exception;

import com.company.project.common.exception.validation.MessageValidator;
import java.text.MessageFormat;
import java.util.Map;

public class InvalidInputException extends CommonException {

    public InvalidInputException(String errorCode, String message) {
        this(errorCode, message, null);
    }

    public InvalidInputException(String errorCode, String message, Map<String, Object> additionalFields) {
        super(errorCode, message, additionalFields);
    }

    public static InvalidInputException of(String errorCode, String message, Object... args) {
        return new InvalidInputException(errorCode, MessageFormat.format(message, args));
    }

    public static InvalidInputException of(Map<String, Object> additionalFields,
                                           String errorCode, String message, Object... args) {
        return new InvalidInputException(errorCode, MessageFormat.format(message, args), additionalFields);
    }

    public static InvalidInputException of(MessageValidator validator) {
        return new InvalidInputException(validator.code(), validator.message());
    }

}
