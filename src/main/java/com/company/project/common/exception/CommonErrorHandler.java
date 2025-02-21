package com.company.project.common.exception;

import static com.company.project.common.exception.constant.CommonErrorCode.NOT_SUPPORTED;
import static com.company.project.common.exception.constant.CommonErrorCode.PARAMETER_INVALID;
import static com.company.project.common.exception.constant.CommonErrorCode.REQUEST_INVALID;
import static com.company.project.common.exception.constant.CommonErrorCode.UNEXPECTED_INTERNAL_ERROR;
import static com.company.project.common.model.constant.CommonConstants.MdcFields.ELAPSED_TIME;
import static com.company.project.common.model.constant.CommonConstants.MdcFields.ERROR_CODE;
import static com.company.project.common.model.constant.CommonConstants.MdcFields.ERROR_HTTP_STATUS;
import static com.company.project.common.model.constant.CommonConstants.MdcFields.ERROR_TYPE;
import static com.company.project.common.model.constant.CommonConstants.MdcFields.URI;
import static net.logstash.logback.argument.StructuredArguments.v;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import com.company.project.common.exception.model.CommonErrorResponse;
import com.company.project.common.model.enums.ErrorType;
import com.company.project.common.util.ErrorUtil;
import com.company.project.common.util.MessageSourceUtil;
import com.company.project.common.util.WebUtil;
import feign.FeignException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ConditionalOnProperty(prefix = "common.error-handler", name = "enabled", matchIfMissing = true)
@RestControllerAdvice
@Slf4j
public class CommonErrorHandler extends ResponseEntityExceptionHandler {

    @Autowired
    private WebUtil webUtil;

    @Autowired
    private MessageSourceUtil messageSourceUtil;

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(InvalidInputException.class)
    public CommonErrorResponse handleInvalidInputException(InvalidInputException ex) {
        addErrorLog(BAD_REQUEST, ex.getErrorCode(), ex.getMessage(), "InvalidInputException");
        return commonErrorResponse(ex);
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(AlreadyExistException.class)
    public CommonErrorResponse handleAlreadyExistException(AlreadyExistException ex) {
        addErrorLog(BAD_REQUEST, ex.getErrorCode(), ex.getMessage(), "AlreadyExistException");
        return commonErrorResponse(ex);
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(DataNotFoundException.class)
    public CommonErrorResponse handleDataNotFoundException(DataNotFoundException ex) {
        addErrorLog(NOT_FOUND, ex.getErrorCode(), ex.getMessage(), "DataNotFoundException");
        return commonErrorResponse(ex);
    }

    @ResponseStatus(NOT_FOUND)
    @ExceptionHandler(ResourceMissingException.class)
    public CommonErrorResponse handleResourceMissingException(ResourceMissingException ex) {
        addErrorLog(NOT_FOUND, ex.getErrorCode(), ex.getMessage(), "ResourceMissingException");
        return commonErrorResponse(ex);
    }

    @ResponseStatus(UNAUTHORIZED)
    @ExceptionHandler(AuthException.class)
    public CommonErrorResponse handleAuthException(AuthException ex) {
        addErrorLog(UNAUTHORIZED, ex.getErrorCode(), ex.getMessage(), "AuthException");
        return commonErrorResponse(ex);
    }

    @ResponseStatus(FORBIDDEN)
    @ExceptionHandler(OperationAuthException.class)
    public CommonErrorResponse handleOperationAuthException(OperationAuthException ex) {
        addErrorLog(FORBIDDEN, ex.getErrorCode(), ex.getMessage(), "OperationAuthException");
        return commonErrorResponse(ex);
    }

    @ResponseStatus(FORBIDDEN)
    @ExceptionHandler(ForbiddenException.class)
    public CommonErrorResponse handleForbiddenException(ForbiddenException ex) {
        addErrorLog(FORBIDDEN, ex.getErrorCode(), ex.getMessage(), "ForbiddenException");
        return commonErrorResponse(ex);
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public CommonErrorResponse handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        addErrorLog(BAD_REQUEST, PARAMETER_INVALID, ex.getMessage(), "MethodArgumentTypeMismatchException");
        return new CommonErrorResponse(requestId(), PARAMETER_INVALID, ex.getMessage());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(MissingRequestHeaderException.class)
    public CommonErrorResponse handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
        addErrorLog(BAD_REQUEST, PARAMETER_INVALID, ex.getMessage(), "MissingRequestHeaderException");
        return new CommonErrorResponse(requestId(), PARAMETER_INVALID, ex.getMessage());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public CommonErrorResponse handleConstraintViolationException(ConstraintViolationException ex) {
        Optional<ConstraintViolation<?>> violation = ex.getConstraintViolations().stream().findAny();
        String errorMessage;
        String errorCode;
        if (violation.isPresent()) {
            String property = ErrorUtil.getPropertyName(violation.get().getPropertyPath());
            String violationMessage = violation.get().getMessage();
            errorMessage = property + " " + messageSourceUtil.getMessage(violationMessage);
            errorCode = messageSourceUtil.getErrorCode(violationMessage, PARAMETER_INVALID);
        } else {
            errorMessage = ex.getMessage();
            errorCode = PARAMETER_INVALID;
        }
        addErrorLog(BAD_REQUEST, errorCode, errorMessage, "ConstraintViolationException");
        return new CommonErrorResponse(requestId(), errorCode, errorMessage);
    }

    @ExceptionHandler(ClientException.class)
    public ResponseEntity<CommonErrorResponse> handleClientException(ClientException ex) {
        HttpStatus httpStatus = httpStatus(ex.getStatus(), INTERNAL_SERVER_ERROR);
        addErrorLog(httpStatus, ex.getErrorCode(), ex.getMessage(), "ClientException");
        CommonErrorResponse errorResponse = new CommonErrorResponse(
                ex.getRequestId(),
                ex.getErrorCode(),
                ex.getMessage(),
                ex.getAdditionalFields());
        return new ResponseEntity<>(errorResponse, httpStatus);
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(FeignException.class)
    public CommonErrorResponse handleFeignException(FeignException ex) {
        HttpStatus httpStatus = httpStatus(ex.status(), INTERNAL_SERVER_ERROR);
        addErrorLog(httpStatus, UNEXPECTED_INTERNAL_ERROR, ex.getMessage(), ex);
        return new CommonErrorResponse(requestId(), UNEXPECTED_INTERNAL_ERROR, ex.getMessage());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler(CommonBadRequestException.class)
    public CommonErrorResponse handleCommonBadRequestException(CommonBadRequestException ex) {
        addErrorLog(BAD_REQUEST, ex.getErrorCode(), ex.getMessage(), ex);
        return new CommonErrorResponse(requestId(), ex.getErrorCode(), ex.getMessage());
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(CommonException.class)
    public CommonErrorResponse handleCommonException(CommonException ex) {
        addErrorLog(INTERNAL_SERVER_ERROR, ex.getErrorCode(), ex.getMessage(), ex);
        return commonErrorResponse(ex);
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public CommonErrorResponse handleAll(Exception ex) {
        addErrorLog(INTERNAL_SERVER_ERROR, UNEXPECTED_INTERNAL_ERROR, ex.getMessage(), ex);
        String errMsg = "Unexpected internal server error";
        return new CommonErrorResponse(requestId(), UNEXPECTED_INTERNAL_ERROR, errMsg/*ex.getMessage()*/);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode statusCode,
                                                                  WebRequest request) {
        Optional<FieldError> fieldError = ex.getBindingResult().getFieldErrors().stream().findAny();
        String errorMessage;
        String errorCode;
        if (fieldError.isPresent()) {
            String fieldName = fieldError.get().getField();
            String fieldMessage = fieldError.get().getDefaultMessage();
            errorMessage = fieldName + " " + messageSourceUtil.getMessage(fieldMessage);
            errorCode = messageSourceUtil.getErrorCode(fieldMessage, REQUEST_INVALID);
        } else {
            errorMessage = ex.getMessage();
            errorCode = REQUEST_INVALID;
        }
        addErrorLog(httpStatus(statusCode), errorCode, errorMessage, "MethodArgumentNotValidException");
        CommonErrorResponse commonErrorResponse = new CommonErrorResponse(requestId(), errorCode, errorMessage);
        return new ResponseEntity<>(commonErrorResponse, headers, statusCode);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
                                                                          HttpHeaders headers,
                                                                          HttpStatusCode statusCode,
                                                                          WebRequest request) {
        String error = ex.getParameterName() + " parameter is missing";
        addErrorLog(httpStatus(statusCode), PARAMETER_INVALID, error, "MissingServletRequestParameterException");
        CommonErrorResponse commonErrorResponse = new CommonErrorResponse(requestId(), PARAMETER_INVALID, error);
        return new ResponseEntity<>(commonErrorResponse, headers, statusCode);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex,
                                                                     HttpHeaders headers,
                                                                     HttpStatusCode statusCode,
                                                                     WebRequest request) {
        addErrorLog(httpStatus(statusCode), PARAMETER_INVALID, ex.getMessage(), "MissingServletRequestPartException");
        CommonErrorResponse errorResponse = new CommonErrorResponse(requestId(), PARAMETER_INVALID, ex.getMessage());
        return new ResponseEntity<>(errorResponse, statusCode);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex,
                                                                     HttpHeaders headers,
                                                                     HttpStatusCode statusCode,
                                                                     WebRequest request) {
        addErrorLog(httpStatus(statusCode), PARAMETER_INVALID, ex.getMessage(), "HttpMediaTypeNotSupportedException");
        CommonErrorResponse errorResponse = new CommonErrorResponse(requestId(), PARAMETER_INVALID, ex.getMessage());
        return new ResponseEntity<>(errorResponse, statusCode);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                         HttpHeaders headers,
                                                                         HttpStatusCode statusCode,
                                                                         WebRequest request) {
        addErrorLog(httpStatus(statusCode), NOT_SUPPORTED, ex.getMessage(), "HttpRequestMethodNotSupportedException");
        CommonErrorResponse errorResponse = new CommonErrorResponse(requestId(), NOT_SUPPORTED, ex.getMessage());
        return new ResponseEntity<>(errorResponse, statusCode);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode statusCode,
                                                                  WebRequest request) {
        String error = "Request not readable";
        addErrorLog(httpStatus(statusCode), REQUEST_INVALID, ex.getMessage(), "HttpMessageNotReadableException");
        CommonErrorResponse commonErrorResponse = new CommonErrorResponse(
                requestId(),
                REQUEST_INVALID,
                error/*ex.getMessage()*/);
        return new ResponseEntity<>(commonErrorResponse, headers, statusCode);
    }

    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex,
                                                                          HttpHeaders headers,
                                                                          HttpStatusCode statusCode,
                                                                          WebRequest request) {
        addErrorLog(httpStatus(statusCode), PARAMETER_INVALID, ex.getMessage(), "ServletRequestBindingException");
        CommonErrorResponse errorResponse = new CommonErrorResponse(requestId(), PARAMETER_INVALID, ex.getMessage());
        return new ResponseEntity<>(errorResponse, statusCode);
    }

    @Override
    protected ResponseEntity<Object> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex,
                                                                          HttpHeaders headers,
                                                                          HttpStatusCode statusCode,
                                                                          WebRequest request) {
        addErrorLog(httpStatus(statusCode), PARAMETER_INVALID, ex.getMessage(), "MaxUploadSizeExceededException");
        CommonErrorResponse errorResponse = new CommonErrorResponse(
                requestId(),
                PARAMETER_INVALID,
                ex.getMostSpecificCause().getMessage());
        return new ResponseEntity<>(errorResponse, statusCode);
    }

    protected CommonErrorResponse commonErrorResponse(CommonException ex) {
        return new CommonErrorResponse(
                requestId(),
                ex.getErrorCode(),
                ex.getMessage(),
                ex.getAdditionalFields());
    }

    protected String requestId() {
        return webUtil.getRequestId();
    }

    protected HttpStatus httpStatus(HttpStatusCode httpStatusCode) {
        return HttpStatus.resolve(httpStatusCode.value());
    }

    protected HttpStatus httpStatus(int httpStatus, HttpStatus defaultHttpStatus) {
        return Optional.ofNullable(HttpStatus.resolve(httpStatus)).orElse(defaultHttpStatus);
    }


    //*** Logging ***//

    protected void addErrorLog(HttpStatus httpStatus, String errorCode, String errorMessage, Throwable ex) {
        log.error("[{}] | HttpStatus: {} | Code: {} | Type: {} | Path: {} | Elapsed time: {} ms | Message: {}",
                v(ERROR_TYPE, ErrorType.ERROR), v(ERROR_HTTP_STATUS, httpStatus), v(ERROR_CODE, errorCode),
                ex.getClass().getTypeName(), v(URI, webUtil.getRequestUri()), v(ELAPSED_TIME, webUtil.getElapsedTime()),
                errorMessage, ex);
    }

    protected void addErrorLog(HttpStatus httpStatus, String errorCode, String errorMessage, String exceptionType) {
        log.error("[{}] | HttpStatus: {} | Code: {} | Type: {} | Path: {} | Elapsed time: {} ms | Message: {}",
                v(ERROR_TYPE, ErrorType.ERROR), v(ERROR_HTTP_STATUS, httpStatus), v(ERROR_CODE, errorCode),
                exceptionType, v(URI, webUtil.getRequestUri()), v(ELAPSED_TIME, webUtil.getElapsedTime()),
                errorMessage);
    }

}
