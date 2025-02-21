package com.company.project.common.exception;

import com.company.project.common.exception.constant.CommonErrorCode;
import java.text.MessageFormat;
import java.util.Map;

public class ResourceMissingException extends CommonException {

    public ResourceMissingException(String message) {
        this(message, null);
    }

    public ResourceMissingException(String message, Map<String, Object> additionalFields) {
        super(CommonErrorCode.RESOURCE_MISSING, message, additionalFields);
    }

    public static ResourceMissingException of(String message, Object... args) {
        return new ResourceMissingException(MessageFormat.format(message, args));
    }

    public static ResourceMissingException of(Map<String, Object> additionalFields, String message, Object... args) {
        return new ResourceMissingException(MessageFormat.format(message, args), additionalFields);
    }

}
