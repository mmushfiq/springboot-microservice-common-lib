package com.company.project.common.util;

import java.text.MessageFormat;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FormatterUtil {

    public static String message(String message, Object... args) {
        return MessageFormat.format(message, args);
    }

}
