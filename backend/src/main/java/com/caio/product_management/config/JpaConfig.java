package com.caio.product_management.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.caio.product_management")
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class JpaConfig {
}