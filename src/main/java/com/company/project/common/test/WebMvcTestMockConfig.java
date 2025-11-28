package com.company.project.common.test;

import com.company.project.common.config.OpenTelemetryConfig;
import io.micrometer.tracing.Tracer;
import java.lang.reflect.Method;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Test-only auto-configuration for the Common Library.
 *
 * <p>Provides Mockito-based mock beans (e.g., {@link Tracer} and
 * {@link OpenTelemetryConfig}) required for {@code @WebMvcTest} and other
 * slice tests. This configuration activates only when Mockito is present on the
 * classpath, thanks to {@link ConditionalOnClass}.</p>
 *
 * <p>The mocks are created reflectively to avoid introducing a compile-time
 * Mockito dependency into the main library module, ensuring they are used only
 * in test environments.</p>
 *
 * <p>This class is typically imported automatically via the
 * {@code WebMvcTest.imports} SPI defined in the Common Library's
 * {@code META-INF/spring} resources.</p>
 */
@ConditionalOnClass(name = "org.mockito.Mockito")
@Configuration
public class WebMvcTestMockConfig {

    @Bean
    public Tracer tracer() {
        return createMock(Tracer.class);
    }

    @Bean
    public OpenTelemetryConfig openTelemetryConfig() {
        return createMock(OpenTelemetryConfig.class);
    }

    @SuppressWarnings("unchecked")
    private <T> T createMock(Class<T> type) {
        try {
            Class<?> mockitoClass = Class.forName("org.mockito.Mockito");
            Method m = mockitoClass.getMethod("mock", Class.class);
            Object mocked = m.invoke(null, type);
            return (T) mocked;
        } catch (Exception ex) {
            throw new RuntimeException("Failed to create mock reflectively", ex);
        }
    }

}