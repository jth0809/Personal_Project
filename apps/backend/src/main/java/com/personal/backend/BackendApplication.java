package com.personal.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.personal.backend.config.data.Dataproperties;
import com.personal.backend.config.jwt.JwtProperties;

@SpringBootApplication
@EnableConfigurationProperties({
    Dataproperties.class,
    JwtProperties.class
})
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
