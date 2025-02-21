package com.company.project.common.util;

import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

@NoArgsConstructor(access = PRIVATE)
public class MaskingUtil {

    public static String maskCardPan(String pan) {
        if (StringUtils.hasLength(pan)) {
            return pan.replaceAll("(\\b\\d{6})(\\d*)(\\d{4})", "$1***$3");
        }
        return pan;
    }

}
