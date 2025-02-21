package com.company.project.common.util;

import static com.company.project.common.model.constant.CommonConstants.HttpAttribute.BEARER;
import static com.company.project.common.model.constant.CommonConstants.HttpAttribute.REQUEST_START_TIME;
import static com.company.project.common.model.constant.CommonConstants.HttpHeader.AUTHORIZATION;
import static com.company.project.common.model.constant.CommonConstants.HttpHeader.PN_CLIENT_IP;
import static com.company.project.common.model.constant.CommonConstants.HttpHeader.PN_REQUEST_ID;
import static com.company.project.common.model.constant.CommonConstants.HttpHeader.PN_USER_AGENT;
import static com.company.project.common.model.constant.CommonConstants.HttpHeader.USER_AGENT;
import static com.company.project.common.model.constant.CommonConstants.HttpHeader.X_REAL_IP;

import com.company.project.common.model.ProjectBasedHeadersDto;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CaseUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

@Component
@Slf4j
public class WebUtil {

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private Tracer tracer;

    public String getRequestId() {
        return Optional.ofNullable(getHttpServletRequest())
                .map(req -> req.getHeader(PN_REQUEST_ID))
                .filter(StringUtils::isNotBlank)
                .orElse(null);
    }

    public String getClientIp() {
        HttpServletRequest request = getHttpServletRequest();
        if (request == null) {
            return "";
        }
        return Optional.ofNullable(request.getHeader(PN_CLIENT_IP))
                .or(() -> Optional.ofNullable(request.getHeader(X_REAL_IP)))
                .orElse(request.getRemoteAddr());
    }

    public String getUserAgent() {
        HttpServletRequest request = getHttpServletRequest();
        if (request == null) {
            return "";
        }
        return Optional.ofNullable(request.getHeader(PN_USER_AGENT))
                .or(() -> Optional.ofNullable(request.getHeader(USER_AGENT)))
                .orElse("");
    }

    public String getProjectBasedHeader(String headerKey) {
        return Optional.ofNullable(getHttpServletRequest())
                .map(req -> req.getHeader(headerKey))
                .orElse("");
    }

    public String getRequestUri() {
        return Optional.ofNullable(getHttpServletRequest())
                .map(req -> req.getMethod() + " " + req.getRequestURI())
                .orElse("");
    }

    public String getRequestUriWithQueryString() {
        return Optional.ofNullable(getHttpServletRequest())
                .map(req -> {
                    String uri = req.getRequestURI();
                    String query = req.getQueryString();
                    return req.getMethod() + " " + (query != null ? uri + "?" + query : uri);
                })
                .orElse("");
    }

    public Map<String, String> getAllRequestHeaders() {
        Map<String, String> headers = new HashMap<>();
        final HttpServletRequest request = getHttpServletRequest();
        if (request == null || request.getHeaderNames() == null) {
            return headers;
        }
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = headerNames.nextElement();
            String value = request.getHeader(key);
            headers.put(key, value);
        }
        return headers;
    }

    public Map<String, String> getNonProjectBasedHeaders() {
        var headers = getAllRequestHeaders();
        headers.entrySet().removeIf(entry -> entry.getKey().toLowerCase().startsWith("pn"));
        return headers;
    }

    public Map<String, String> getProjectBasedHeaders() {
        Map<String, String> headers = new HashMap<>();
        HttpServletRequest request = getHttpServletRequest();
        if (request == null || request.getHeaderNames() == null) {
            return headers;
        }
        request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
            if (headerName.toLowerCase().startsWith("pn")) {
                headers.put(headerName.toLowerCase(), request.getHeader(headerName));
            }
        });
        return headers;
    }

    public ProjectBasedHeadersDto getProjectBasedHeadersAsDto() {
        final ProjectBasedHeadersDto projectBasedHeadersDto = new ProjectBasedHeadersDto();
        getProjectBasedHeaders().forEach((header, value) -> {
            try {
                String setterMethodName = CaseUtils.toCamelCase(header, false, '-').replace("pn", "set");
                Method callingMethod = ProjectBasedHeadersDto.class.getDeclaredMethod(setterMethodName, String.class);
                callingMethod.invoke(projectBasedHeadersDto, value);
            } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
                log.warn("Exception happened while creating ProjectBasedHeadersDto using refection; "
                        + "header: {}, message: {}", header, ex.toString());
            }
        });
        log.trace("{}", projectBasedHeadersDto);

        return projectBasedHeadersDto;
    }

    public Long getElapsedTime() {
        return Optional.ofNullable(getHttpServletRequest())
                .map(req -> String.valueOf(req.getAttribute(REQUEST_START_TIME)))
                .filter(StringUtils::isNumeric)
                .map(t -> System.currentTimeMillis() - Long.parseLong(t))
                .orElse(-1L);
    }

    public String getBearerTokenFromAuthorizationHeader() {
        return Optional.ofNullable(getHttpServletRequest())
                .map(request -> request.getHeader(AUTHORIZATION))
                .filter(token -> token.startsWith(BEARER))
                .orElse(null);
    }

    public String getRequestBody() {
        try {
            return Optional.ofNullable(getHttpServletRequest())
                    .filter(ContentCachingRequestWrapper.class::isInstance)
                    .map(ContentCachingRequestWrapper.class::cast)
                    .map(ContentCachingRequestWrapper::getContentAsByteArray)
                    .map(String::new)
                    .orElse("[null]");
        } catch (Exception e) {
            log.error("{} error occurs while getting request body for uri {}", e.getMessage(), getRequestUri(), e);
            return "[failed]";
        }
    }

    public String getTraceId() {
        Span span = tracer.currentSpan();
        return span != null ? span.context().traceId() : "";
    }

    public String getSpanId() {
        Span span = tracer.currentSpan();
        return span != null ? span.context().spanId() : "";
    }

    private HttpServletRequest getHttpServletRequest() {
        try {
            if (httpServletRequest != null && httpServletRequest.getHeaderNames() != null) {
                return httpServletRequest;
            }
        } catch (Exception ex) {
            log.debug("[WebUtil.getHttpServletRequest] : {}", ex.getMessage());
        }

        return null;
    }

}
