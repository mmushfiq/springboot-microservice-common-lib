package com.company.project.common.exception;

import com.company.project.common.exception.constant.CommonErrorCode;
import java.text.MessageFormat;
import java.util.Map;

public class AuthException extends CommonException {

    public AuthException(String message) {
        this(CommonErrorCode.UNAUTHORIZED, message);
    }

    public AuthException(String message, Map<String, Object> additionalFields) {
        this(CommonErrorCode.UNAUTHORIZED, message, additionalFields);
    }

    public AuthException(String errorCode, String message) {
        this(errorCode, message, null);
    }

    public AuthException(String errorCode, String message, Map<String, Object> additionalFields) {
        super(errorCode, message, additionalFields);
    }

    public static AuthException of(String message, Object... args) {
        return new AuthException(MessageFormat.format(message, args));
    }

    public static AuthException of(Map<String, Object> additionalFields, String message, Object... args) {
        return new AuthException(MessageFormat.format(message, args), additionalFields);
    }

    public static AuthException withErrorCode(String errorCode, String message, Object... args) {
        return new AuthException(errorCode, MessageFormat.format(message, args));
    }

    public static AuthException withErrorCode(Map<String, Object> additionalFields, String errorCode,
                                              String message, Object... args) {
        return new AuthException(errorCode, MessageFormat.format(message, args), additionalFields);
    }

}
