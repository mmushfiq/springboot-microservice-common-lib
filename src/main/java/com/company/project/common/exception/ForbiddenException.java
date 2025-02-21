package com.company.project.common.exception;

import com.company.project.common.exception.constant.CommonErrorCode;
import java.text.MessageFormat;
import java.util.Map;

public class ForbiddenException extends CommonException {

    public ForbiddenException(String message) {
        this(message, null);
    }

    public ForbiddenException(String message, Map<String, Object> additionalFields) {
        super(CommonErrorCode.FORBIDDEN, message, additionalFields);
    }

    public static ForbiddenException of(String message, Object... args) {
        return new ForbiddenException(MessageFormat.format(message, args));
    }

    public static ForbiddenException of(Map<String, Object> additionalFields, String message, Object... args) {
        return new ForbiddenException(MessageFormat.format(message, args), additionalFields);
    }

}
