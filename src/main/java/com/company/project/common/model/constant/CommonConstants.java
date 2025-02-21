package com.company.project.common.model.constant;

import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommonConstants {

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class HttpAttribute {
        public static final String REQUEST_START_TIME = "request_start_time";
        public static final String BEARER = "Bearer ";
        public static final String BASIC = "Basic ";
        public static final String CONTENT_TYPE = "Content-Type";
        public static final String ACCEPT = "Accept";
        public static final String DEFAULT_LANGUAGE = "en";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class HttpHeader {
        public static final String X_FORWARDED_FOR = "X-Forwarded-For";
        public static final String X_REAL_IP = "X-Real-IP";
        public static final String USER_AGENT = "User-Agent";
        public static final String AUTHORIZATION = "Authorization";
        public static final String PN_REQUEST_ID = "pn-request-id";
        public static final String PN_CUSTOMER_ID = "pn-customer-id";
        public static final String PN_USER_ID = "pn-user-id";
        public static final String PN_USER_ROLE = "pn-user-role";
        public static final String PN_EMAIL = "pn-email";
        public static final String PN_DEVICE_ID = "pn-device-id";
        public static final String PN_DEVICE_MODEL = "pn-device-model";
        public static final String PN_APP_VERSION = "pn-app-version";
        public static final String PN_ORIGIN_SERVICE = "pn-origin-service";
        public static final String PN_USER_AGENT = "pn-user-agent";
        public static final String PN_CLIENT_IP = "pn-client-ip";
        public static final String PN_LANGUAGE = "pn-lang";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class MdcFields {
        public static final String EVENT_ID = "pn-event-id";
        public static final String EVENT_TYPE = "pn-event-type";
        public static final String URI = "pn-uri";
        public static final String ELAPSED_TIME = "pn-elapsed-time";
        public static final String ERROR_HTTP_STATUS = "pn-error-http-status";
        public static final String ERROR_CODE = "pn-error-code";
        public static final String ERROR_TYPE = "pn-error-type";
        public static final String HTTP_MESSAGE_TYPE = "pn-http-message-type";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class SecretName {
        public static final String JAEGER_URL = "JAEGER_URL";
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Tracing {
        public static final List<String> DEFAULT_EXCLUDED_JAEGER_PATTERNS =
                List.of("/actuator**/**", "/swagger**/**", "/**/api-docs**/**");
    }

}
