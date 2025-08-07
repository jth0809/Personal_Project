package com.personal.backend.config.oci;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "oci")
public record OciProperties(String bucketName, String namespace) {
}
