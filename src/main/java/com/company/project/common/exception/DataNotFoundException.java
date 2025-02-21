package com.company.project.common.exception;

import com.company.project.common.exception.constant.CommonErrorCode;
import java.text.MessageFormat;
import java.util.Map;

public class DataNotFoundException extends CommonException {

    public DataNotFoundException(String message) {
        this(message, null);
    }

    public DataNotFoundException(String message, Map<String, Object> additionalFields) {
        super(CommonErrorCode.DATA_NOT_FOUND, message, additionalFields);
    }

    public static DataNotFoundException of(String message, Object... args) {
        return new DataNotFoundException(MessageFormat.format(message, args));
    }

    public static DataNotFoundException of(Map<String, Object> additionalFields, String message, Object... args) {
        return new DataNotFoundException(MessageFormat.format(message, args), additionalFields);
    }

}
