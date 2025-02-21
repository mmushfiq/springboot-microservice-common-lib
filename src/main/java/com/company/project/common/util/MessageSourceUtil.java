package com.company.project.common.util;

import static com.company.project.common.model.constant.CommonConstants.HttpHeader.PN_LANGUAGE;

import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessageSourceUtil {

    private final MessageSource messageSource;
    private final WebUtil webUtil;

    public String getMessage(String key) {
        return getMessage(key, (Object[]) null);
    }

    public String getMessage(String key, String lang) {
        return getMessage(key, null, lang);
    }

    public String getMessage(String key, Object[] arg) {
        try {
            return messageSource.getMessage(key, arg, Locale.of(webUtil.getProjectBasedHeader(PN_LANGUAGE)));
        } catch (NoSuchMessageException ex) {
            return key;
        }
    }

    public String getMessage(String key, Object[] arg, String lang) {
        try {
            return messageSource.getMessage(key, arg, Locale.of(lang));
        } catch (NoSuchMessageException ex) {
            return key;
        }
    }

    public String getErrorCode(String validationMessage, String defaultCode) {
        try {
            String errorCodeKey = validationMessage.replace("message", "code");
            return messageSource.getMessage(errorCodeKey, null, Locale.of(webUtil.getProjectBasedHeader(PN_LANGUAGE)));
        } catch (NullPointerException | NoSuchMessageException ex) {
            return defaultCode;
        }
    }

}
