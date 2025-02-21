package com.company.project.common.config;

import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@ConditionalOnProperty(prefix = "common.mapper", name = "enabled", matchIfMissing = true)
@MapperConfig(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommonMapperConfig {
}
