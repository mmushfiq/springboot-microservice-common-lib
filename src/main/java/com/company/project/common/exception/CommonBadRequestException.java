package com.company.project.common.exception;

import lombok.Getter;

@Getter
public class CommonBadRequestException extends RuntimeException {

    private final String errorCode;
    private final String message;

    protected CommonBadRequestException(String errorCode, String message) {
        super(errorCode);
        this.errorCode = errorCode;
        this.message = message;
    }

}
