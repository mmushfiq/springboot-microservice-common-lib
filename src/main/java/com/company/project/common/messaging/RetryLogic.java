package com.company.project.common.messaging;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RetryLogic {

    private static final List<String> NON_RETRYABLE_EXCEPTIONS = List.of(
            "org.springframework.amqp.AmqpRejectAndDontRequeueException",
            "org.springframework.amqp.support.converter.MessageConversionException",
            "org.springframework.messaging.converter.MessageConversionException",
            "org.springframework.messaging.handler.invocation.MethodArgumentResolutionException",
            "java.lang.NoSuchMethodException",
            "java.lang.ClassCastException",
            "com.company.project.common.exception.CommonBadRequestException",
            "com.company.project.common.exception.CommonException",
            "com.company.project.common.exception.ClientException"
    );

    public static Map<Class<? extends Throwable>, Boolean> getNonRetryableExceptions() {
        return nonRetryableExceptions(NON_RETRYABLE_EXCEPTIONS.stream());
    }

    public static Map<Class<? extends Throwable>, Boolean> addAndGetNonRetryableExceptions(List<String> exceptions) {
        return nonRetryableExceptions(Stream.concat(NON_RETRYABLE_EXCEPTIONS.stream(), exceptions.stream()));
    }

    public static Map<Class<? extends Throwable>, Boolean> nonRetryableExceptions(Stream<String> exceptions) {
        return exceptions
                .map(RetryLogic::getExceptionClass)
                .collect(Collectors.toMap(clazz -> clazz, clazz -> false));
    }

    private static Class<? extends Throwable> getExceptionClass(String className) {
        try {
            return Class.forName(className).asSubclass(Throwable.class);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("Class could not be found with name: " + className);
        }
    }

}
