package com.company.project.common.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/**
 * Auto-configuration entry point for the Common Library.
 *
 * <p>Registers all Common Library components by performing a component scan on
 * {@code com.company.project.common}. This allows applications to use the
 * library without manually adding a {@code @ComponentScan} in their own code.
 *
 * <p>Loaded automatically when the Common Library is on the classpath and
 * Spring Boot auto-configuration is enabled.
 */
@AutoConfiguration
@ComponentScan(basePackages = {"com.company.project.common"})
public class CommonLibAutoConfig {
}
