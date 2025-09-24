package com.example.jsh.fap;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "fap")
public class FAPHttpProperties {
    private String baseUrl;
    private int timeoutSec = 480;
}
