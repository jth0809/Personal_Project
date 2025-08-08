package com.personal.backend.config.oauth; // 적절한 config 패키지에 위치

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(Oauth2 oauth2) {

    public record Oauth2(String redirectUri) {}
}