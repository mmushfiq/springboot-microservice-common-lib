package com.company.project.common.util;

import jakarta.validation.Path;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ErrorUtil {

    public static String getPropertyName(Path propertyPath) {
        String propertyName = null;
        for (Path.Node node : propertyPath) {
            propertyName = node.getName();
        }
        return propertyName != null ? propertyName : propertyPath.toString();
    }

}
