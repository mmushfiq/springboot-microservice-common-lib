package com.company.project.common.interceptor;

import com.company.project.common.model.constant.CommonConstants.HttpAttribute;
import com.company.project.common.util.WebUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@ConditionalOnProperty(prefix = "common.interceptor", name = "enabled", matchIfMissing = true)
@Component
@RequiredArgsConstructor
@Slf4j
public class CommonRequestInterceptor implements HandlerInterceptor {

    private final WebUtil webUtil;

    @Override
    public boolean preHandle(HttpServletRequest req, HttpServletResponse res, Object handler) throws Exception {
        log.trace("CommonRequestInterceptor is calling for uri: {} {}", req.getMethod(), req.getRequestURI());

        req.setAttribute(HttpAttribute.REQUEST_START_TIME, System.currentTimeMillis());
        final var projectBasedHeaders = webUtil.getProjectBasedHeaders();
        log.debug("Project Based Headers in CommonRequestInterceptor: {}", projectBasedHeaders);
        log.trace("Non Project Based Headers in CommonRequestInterceptor: {}", webUtil.getNonProjectBasedHeaders());
        projectBasedHeaders.forEach((header, value) -> MDC.put(header.toLowerCase(), value));

        return HandlerInterceptor.super.preHandle(req, res, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest req, HttpServletResponse res, Object handler, Exception ex) {
        MDC.clear();
        req.removeAttribute(HttpAttribute.REQUEST_START_TIME);
        log.trace("CommonRequestInterceptor ended for uri: {} {}. MDC cleared", req.getMethod(), req.getRequestURI());
    }

}
