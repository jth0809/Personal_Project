package com.personal.backend.config.data;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "datainit")
public record Dataproperties(String password) {
}
