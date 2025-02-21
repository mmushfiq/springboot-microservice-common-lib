package com.company.project.common.exception;

import com.company.project.common.exception.constant.CommonErrorCode;
import java.text.MessageFormat;
import java.util.Map;

public class AlreadyExistException extends CommonException {

    public AlreadyExistException(String message) {
        this(message, null);
    }

    public AlreadyExistException(String message, Map<String, Object> additionalFields) {
        super(CommonErrorCode.ALREADY_EXIST, message, additionalFields);
    }

    public static AlreadyExistException of(String message, Object... args) {
        return new AlreadyExistException(MessageFormat.format(message, args));
    }

    public static AlreadyExistException of(Map<String, Object> additionalFields, String message, Object... args) {
        return new AlreadyExistException(MessageFormat.format(message, args), additionalFields);
    }

}
