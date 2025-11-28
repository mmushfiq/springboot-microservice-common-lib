package com.company.project.common.test;

import com.company.project.common.config.CommonLibAutoConfig;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * Import selector that automatically loads Common Library configuration classes
 * during {@code @WebMvcTest} tests.
 *
 * <p>This class is registered via Spring Boot’s Test Import Selector SPI
 * ({@code META-INF/spring/org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest.imports})
 * so that the following configurations are applied only in MVC slice tests:
 * <ul>
 *   <li>{@code CommonLibAutoConfig} – main Common Library auto-configuration</li>
 *   <li>{@code WebMvcTestMockConfig} – test-only mocks/stubs (e.g., Tracer)</li>
 * </ul>
 * This allows consumer applications to use {@code @WebMvcTest} without manually
 * importing Common Library test configuration.
 */
@Slf4j
public class WebMvcTestImportSelector implements ImportSelector {

    @NotNull
    @Override
    public String[] selectImports(@NotNull AnnotationMetadata metadata) {
        log.info("WebMvcTestImportSelector loaded");
        return new String[] {
                CommonLibAutoConfig.class.getName(),
                WebMvcTestMockConfig.class.getName()
        };
    }

}
