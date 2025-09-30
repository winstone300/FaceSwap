package com.example.jsh.fap;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.time.Duration;

@Data
@Component
@ConfigurationProperties(prefix = "fap")
public class FAPHttpProperties {
    private final Http http = new Http();

    @Data
    public static class Http {
        private String baseUrl;
        private Duration connectTimeout = Duration.ofSeconds(5);
        private Duration readTimeout = Duration.ofSeconds(120);

        /**
         * Timeout for leasing a connection from the internal pool. Defaults to the connect timeout
         * when not explicitly configured.
         */
        private Duration connectionRequestTimeout;

        public Duration getConnectionRequestTimeout() {
            return connectionRequestTimeout != null ? connectionRequestTimeout : connectTimeout;
        }
    }
}
