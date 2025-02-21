package com.company.project.common.exception;

import com.company.project.common.exception.constant.CommonErrorCode;
import com.company.project.common.exception.constant.CommonErrorMessage;
import java.text.MessageFormat;
import java.util.Map;

public class OperationAuthException extends CommonException {

    public OperationAuthException(String message) {
        this(message, null);
    }

    public OperationAuthException(String message, Map<String, Object> additionalFields) {
        super(CommonErrorCode.OPERATION_AUTH_ERROR, message, additionalFields);
    }

    public static OperationAuthException of() {
        return new OperationAuthException(CommonErrorMessage.OPERATION_AUTH_ERROR_MESSAGE);
    }

    public static OperationAuthException of(String message, Object... args) {
        return new OperationAuthException(MessageFormat.format(message, args));
    }

    public static OperationAuthException of(Map<String, Object> additionalFields) {
        return new OperationAuthException(CommonErrorMessage.OPERATION_AUTH_ERROR_MESSAGE, additionalFields);
    }

    public static OperationAuthException of(Map<String, Object> additionalFields, String message, Object... args) {
        return new OperationAuthException(MessageFormat.format(message, args), additionalFields);
    }

}
