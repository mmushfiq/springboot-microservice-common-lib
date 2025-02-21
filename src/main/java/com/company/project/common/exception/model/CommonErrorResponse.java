package com.company.project.common.exception.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonErrorResponse {

    private String requestId;
    private String errorCode;
    private String message;
    private Map<String, Object> additionalFields;

    public CommonErrorResponse(String requestId, String errorCode, String message) {
        this(requestId, errorCode, message, null);
    }

    public CommonErrorResponse(String requestId, String errorCode, String message,
                               Map<String, Object> additionalFields) {
        this.requestId = requestId;
        this.errorCode = errorCode;
        this.message = message;
        this.additionalFields = additionalFields;
    }

}
