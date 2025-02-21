package com.company.project.common.aop;

import static com.company.project.common.model.constant.CommonConstants.MdcFields.ELAPSED_TIME;
import static com.company.project.common.model.constant.CommonConstants.MdcFields.ERROR_CODE;
import static com.company.project.common.model.constant.CommonConstants.MdcFields.ERROR_TYPE;
import static com.company.project.common.model.constant.CommonConstants.MdcFields.EVENT_ID;
import static com.company.project.common.model.constant.CommonConstants.MdcFields.EVENT_TYPE;
import static com.company.project.common.model.constant.CommonConstants.MdcFields.HTTP_MESSAGE_TYPE;
import static com.company.project.common.model.constant.CommonConstants.MdcFields.URI;
import static net.logstash.logback.argument.StructuredArguments.v;

import com.company.project.common.aop.annotation.ControllerLog;
import com.company.project.common.messaging.BaseEvent;
import com.company.project.common.messaging.BaseResultEvent;
import com.company.project.common.model.enums.ErrorType;
import com.company.project.common.util.LoggerUtil;
import com.company.project.common.util.WebUtil;
import java.lang.reflect.Method;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@ConditionalOnProperty(prefix = "common.logging", name = "enabled", matchIfMissing = true)
@Component
@Aspect
@Slf4j
@RequiredArgsConstructor
public class CommonLoggingAspect {

    private final WebUtil webUtil;

    @Around("execution(* com.company.project..*.controller..*(..)))")
    public Object logControllerEndpoints(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        final String uri = webUtil.getRequestUriWithQueryString();
        final Map<String, String> pnHeaders = webUtil.getProjectBasedHeaders();

        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();
        Method method = methodSignature.getMethod();
        ControllerLog controllerLog = method.isAnnotationPresent(ControllerLog.class)
                ? method.getAnnotation(ControllerLog.class) : null;

        if (controllerLog != null && controllerLog.excludeRequestData()) {
            log.debug("[{}]  | Uri: {} [{}.{}] | PN Headers: {}", v(HTTP_MESSAGE_TYPE, "REQUEST"),
                    v(URI, uri), className, methodName, pnHeaders.toString());
        } else {
            var params = LoggerUtil.getParamsAsMap(methodSignature.getParameterNames(), proceedingJoinPoint.getArgs());
            log.debug("[{}]  | Uri: {} [{}.{}] | PN Headers: {} | Params: {}", v(HTTP_MESSAGE_TYPE, "REQUEST"),
                    v(URI, uri), className, methodName, pnHeaders.toString(), params);
        }

        long start = System.currentTimeMillis();
        Object result = proceedingJoinPoint.proceed();
        long elapsedTime = System.currentTimeMillis() - start;

        if (controllerLog != null && controllerLog.excludeResponseData()) {
            log.debug("[{}] | Uri: {} [{}.{}] | Elapsed time: {} ms", v(HTTP_MESSAGE_TYPE, "RESPONSE"),
                    v(URI, uri), className, methodName, v(ELAPSED_TIME, elapsedTime));
        } else {
            log.debug("[{}] | Uri: {} [{}.{}] | Elapsed time: {} ms | Result: {}", v(HTTP_MESSAGE_TYPE, "RESPONSE"),
                    v(URI, uri), className, methodName, v(ELAPSED_TIME, elapsedTime), result);
        }

        return result;
    }

    @AfterThrowing(pointcut = "@annotation(org.springframework.scheduling.annotation.Async)", throwing = "throwable")
    public void logAsyncException(JoinPoint joinPoint, Throwable throwable) {
        log.error("[{}] | Code: {} | Type: {} | Method: [{}] | Message: {}",
                v(ERROR_TYPE, ErrorType.ASYNC_ERROR), v(ERROR_CODE, errorCode(throwable)),
                throwable.getClass().getTypeName(), methodName(joinPoint), throwable.getMessage(), throwable);
    }

    @After("execution(* com.company.project..*.messaging.MessageProducer.*(..)) && args(baseEvent)")
    public void logProducerBaseEventData(JoinPoint joinPoint, BaseEvent<?> baseEvent) {
        logMessagingEvent(joinPoint, baseEvent.getEventId(), "PRODUCER");
    }

    @After("execution(* com.company.project..*.messaging.MessageProducer.*(..)) && args(.., baseResultEvent)")
    public void logProducerBaseResultEventData(JoinPoint joinPoint, BaseResultEvent<?> baseResultEvent) {
        logMessagingEvent(joinPoint, baseResultEvent.getEventId(), "PRODUCER");
    }

    @Around("execution(* com.company.project..*.messaging.MessageConsumer.*(..)) && args(baseEvent)")
    public Object logConsumerBaseEventData(ProceedingJoinPoint proceedingJoinPoint,
                                           BaseEvent<?> baseEvent) throws Throwable {
        return logConsumerEventData(proceedingJoinPoint, baseEvent.getHeaders(), baseEvent.getEventId());
    }

    @Around("execution(* com.company.project..*.messaging.MessageConsumer.*(..)) && args(baseResultEvent)")
    public Object logConsumerBaseResultEventData(ProceedingJoinPoint proceedingJoinPoint,
                                                 BaseResultEvent<?> baseResultEvent) throws Throwable {
        return logConsumerEventData(proceedingJoinPoint, baseResultEvent.getHeaders(), baseResultEvent.getEventId());
    }

    private Object logConsumerEventData(ProceedingJoinPoint proceedingJoinPoint,
                                        Map<String, String> headers,
                                        String eventId) throws Throwable {
        try {
            addToMdc(headers);
            logMessagingEvent(proceedingJoinPoint, eventId, "CONSUMER");
            return proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            logMessagingEventError(proceedingJoinPoint, eventId, throwable);
            throw throwable;
        } finally {
            removeFromMdc(headers);
        }
    }

    private void logMessagingEvent(JoinPoint joinPoint, String eventId, String eventType) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Map<String, Object> params = LoggerUtil.getParamsAsMap(methodSignature.getParameterNames(), joinPoint.getArgs());
        log.debug("[EVENT]  | EventType: {} | EventId: {} | [{}] | Params: {}",
                v(EVENT_TYPE, eventType), v(EVENT_ID, eventId), methodName(joinPoint), params);
    }

    private void logMessagingEventError(JoinPoint joinPoint, String eventId, Throwable throwable) {
        log.error("[{}] | Code: {} | Type: {} | Method: [{}] | EventId: {} | Message: {}",
                v(ERROR_TYPE, ErrorType.EVENT_ERROR), v(ERROR_CODE, errorCode(throwable)),
                throwable.getClass().getTypeName(), methodName(joinPoint), v(EVENT_ID, eventId), throwable.getMessage(),
                throwable);
    }

    private String errorCode(Throwable throwable) {
        try {
            Method getErrorCodeMethod = throwable.getClass().getMethod("getErrorCode");
            return (String) getErrorCodeMethod.invoke(throwable);
        } catch (Exception e) {
            return "";
        }
    }

    private String methodName(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();
        return String.join(".", className, methodName);
    }

    private void addToMdc(Map<String, String> headers) {
        if (!CollectionUtils.isEmpty(headers)) {
            headers.forEach((header, value) -> MDC.put(header.toLowerCase(), value));
        }
    }

    private void removeFromMdc(Map<String, String> headers) {
        if (!CollectionUtils.isEmpty(headers)) {
            headers.forEach((header, value) -> MDC.remove(header.toLowerCase()));
        }
    }

}
