package com.company.project.common.logging;

import com.company.project.common.util.MaskingUtil;
import com.fasterxml.jackson.core.JsonStreamContext;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.logstash.logback.mask.ValueMasker;
import org.springframework.util.StringUtils;

public class CustomLogFieldMasker implements ValueMasker {

    private static final List<String> SENSITIVE_FIELDS = List.of("pan", "value", "receiverPan", "senderPan", "token");

    private Pattern panPattern;
    private Pattern messagePattern;

    public void setPanProperty(String panProperty) {
        this.panPattern = buildPattern(panProperty);
    }

    public void setMessageProperty(String messageProperty) {
        this.messagePattern = buildPattern(messageProperty);
    }

    private Pattern buildPattern(String pattern) {
        return !StringUtils.hasLength(pattern) ? null : Pattern.compile(pattern, Pattern.MULTILINE);
    }

    @Override
    public Object mask(JsonStreamContext context, Object value) {

        if (Objects.isNull(value)) {
            return null;
        }

        final String contextName = context.getCurrentName();

        if ("message".equals(contextName)) {
            StringBuilder message = new StringBuilder(value.toString());
            replaceByPanProperty(message);
            return  replaceByMessageProperty(message);
        }

        if (SENSITIVE_FIELDS.contains(contextName)) {
            return MaskingUtil.maskCardPan(value.toString());
        }

        if ("cvv".equals(contextName)) {
            return "***";
        }

        return null;
    }

    private void replaceByPanProperty(StringBuilder message) {
        if (panPattern != null) {
            Matcher matcher = panPattern.matcher(message);
            while (matcher.find()) {
                if (matcher.group(2) != null) {
                    for (int i = matcher.start(2) + 6; i < matcher.end(2) - 4; i++) {
                        message.setCharAt(i, '*');
                    }
                }
            }
        }
    }

    private String replaceByMessageProperty(StringBuilder message) {
        if (messagePattern != null) {
            Matcher matcher = messagePattern.matcher(message);
            while (matcher.find()) {
                for (int groupIndex = 1; groupIndex <= matcher.groupCount(); groupIndex++) {
                    if (matcher.group(groupIndex) != null) {
                        for (int i = matcher.start(groupIndex); i < matcher.end(groupIndex); i++) {
                            message.setCharAt(i, '*');
                        }
                    }
                }
            }
        }
        return message.toString();
    }

}
