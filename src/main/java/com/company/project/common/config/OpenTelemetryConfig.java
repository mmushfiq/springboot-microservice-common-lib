package com.company.project.common.config;

import static com.company.project.common.model.constant.CommonConstants.Tracing.DEFAULT_EXCLUDED_JAEGER_PATTERNS;
import static io.opentelemetry.semconv.ResourceAttributes.SERVICE_NAME;
import static io.opentelemetry.semconv.ResourceAttributes.SERVICE_NAMESPACE;

import com.company.project.common.model.constant.CommonConstants.SecretName;
import io.micrometer.observation.ObservationPredicate;
import io.opentelemetry.api.common.Attributes;
import io.opentelemetry.exporter.otlp.trace.OtlpGrpcSpanExporter;
import io.opentelemetry.sdk.OpenTelemetrySdk;
import io.opentelemetry.sdk.resources.Resource;
import io.opentelemetry.sdk.trace.SdkTracerProvider;
import io.opentelemetry.sdk.trace.export.BatchSpanProcessor;
import io.opentelemetry.sdk.trace.samplers.Sampler;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.tracing.TracingProperties;
import org.springframework.boot.actuate.autoconfigure.tracing.TracingProperties.Propagation.PropagationType;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.server.observation.ServerRequestObservationContext;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.CollectionUtils;
import org.springframework.util.PathMatcher;

@ConditionalOnProperty(prefix = "common.opentelemetry", name = "enabled", matchIfMissing = true)
@Configuration
@Slf4j
public class OpenTelemetryConfig {

    private final String applicationName;
    private final String jaegerUrl;
    private final List<String> jaegerExcludedPatterns;
    private final PathMatcher pathMatcher;
    private final Environment environment;

    public OpenTelemetryConfig(@Value("${spring.application.name}") String applicationName,
                               @Value("${common.opentelemetry.jaeger-url:}") String jaegerUrl,
                               @Value("${common.opentelemetry.jaeger-excluded-patterns:}")
                               List<String> jaegerExcludedPatterns,
                               Environment environment,
                               TracingProperties tracingProperties) {
        this.applicationName = applicationName;
        this.jaegerUrl = jaegerUrl;
        this.jaegerExcludedPatterns = jaegerExcludedPatterns;
        this.pathMatcher = new AntPathMatcher();
        this.environment = environment;
        customizeTracingProperties(tracingProperties);
    }

    @Bean
    public OpenTelemetrySdk openTelemetrySdk() {
        final Resource resource = Resource.create(Attributes.of(
                SERVICE_NAME, "PN/".concat(applicationName),
                SERVICE_NAMESPACE, "PN"));

        String spanExporterEndpoint = getSpanExporterEndpoint(jaegerUrl, environment);
        log.trace("spanExporterEndpoint=[{}]", spanExporterEndpoint);
        final OtlpGrpcSpanExporter otlpGrpcSpanExporter = OtlpGrpcSpanExporter.builder()
                .setEndpoint(spanExporterEndpoint)
                .build();

        final SdkTracerProvider sdkTracerProvider = SdkTracerProvider.builder()
                .setSampler(Sampler.alwaysOn())
                .setResource(Resource.getDefault().merge(resource))
                .addSpanProcessor(BatchSpanProcessor.builder(otlpGrpcSpanExporter).build())
                .build();

        return OpenTelemetrySdk.builder()
                .setTracerProvider(sdkTracerProvider)
                .build();
    }

    @Bean
    public ObservationPredicate observationPredicate() {
        log.debug("ObservationPredicate bean created");
        return (name, context) -> {
            if (name.equals("http.server.requests")
                    && context instanceof ServerRequestObservationContext serverContext) {
                return !isExcludedUri(serverContext.getCarrier().getRequestURI());
            }
            return true;
        };
    }

    private String getSpanExporterEndpoint(String jaegerUrl, Environment environment) {
        if (StringUtils.isBlank(jaegerUrl) || jaegerUrl.trim().equals("null")) {
            return environment.getProperty(SecretName.JAEGER_URL, "http://localhost:4317");
        }
        return jaegerUrl;
    }

    private void customizeTracingProperties(TracingProperties tracingProperties) {
        tracingProperties.getPropagation().setType(List.of(PropagationType.B3_MULTI));
        tracingProperties.getSampling().setProbability(1.0F);
        tracingProperties.getBrave().setSpanJoiningSupported(false);
    }

    private boolean isExcludedUri(String uri) {
        List<String> excludedPatterns = CollectionUtils.isEmpty(jaegerExcludedPatterns)
                ? DEFAULT_EXCLUDED_JAEGER_PATTERNS : jaegerExcludedPatterns;
        return uri != null && excludedPatterns.stream().anyMatch(pattern -> pathMatcher.match(pattern, uri));
    }

}