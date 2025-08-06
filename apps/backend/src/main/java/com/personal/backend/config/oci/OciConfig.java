package com.personal.backend.config.oci;

import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.io.IOException;

@Configuration
@Profile("!test")
public class OciConfig {

    @Bean
    public ObjectStorage objectStorageClient() throws IOException {
        
        final ConfigFileReader.ConfigFile configFile = ConfigFileReader.parse("/etc/oci/config", "DEFAULT");
        
        final AuthenticationDetailsProvider provider = new ConfigFileAuthenticationDetailsProvider(configFile);
        
        return ObjectStorageClient.builder().build(provider);
    }
}