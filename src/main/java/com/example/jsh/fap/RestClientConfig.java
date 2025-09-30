package com.example.jsh.fap;

import lombok.RequiredArgsConstructor;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.apache.hc.core5.util.Timeout;

@Configuration
@RequiredArgsConstructor
public class RestClientConfig {

    private final FAPHttpProperties props;

    @Bean
    public RestTemplate fapRestTemplate() {
        var http = props.getHttp();

        Timeout connectTimeout = Timeout.of(http.getConnectTimeout());
        Timeout readTimeout = Timeout.of(http.getReadTimeout());
        Timeout leaseTimeout = Timeout.of(http.getConnectionRequestTimeout());

        RequestConfig rc = RequestConfig.custom()
                .setConnectTimeout(connectTimeout)
                .setResponseTimeout(readTimeout)
                .setConnectionRequestTimeout(leaseTimeout)
                .build();

        //Apache HttpClient customizing
        CloseableHttpClient client = HttpClients.custom()
                .setDefaultRequestConfig(rc)
                .build();

        //RestTemplate이 Apache HttpClient를 내부적으로 사용하도록 연결
        HttpComponentsClientHttpRequestFactory factory =
                new HttpComponentsClientHttpRequestFactory(client);
        factory.setConnectTimeout(http.getConnectTimeout());
        factory.setReadTimeout(http.getReadTimeout());

        // (선택) Spring 팩토리에도 한 번 더 설정하고 싶다면 Duration로:
        // import java.time.Duration;
        // f.setConnectTimeout(Duration.ofSeconds(props.getTimeoutSec()));
        // f.setReadTimeout(Duration.ofSeconds(props.getTimeoutSec()));

        return new RestTemplate(factory);
    }
}
