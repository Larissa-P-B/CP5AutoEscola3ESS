package br.com.fiap3ess.autoescola3ess.infra.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

@Configuration
public class ViaCepConfig {
    @Bean
    public RestClient viaCepRestClient(
            @Value("${app.viacep.base-url}") String baseUrl,
            @Value("${app.viacep.connect-timeout-ms}") int connectTimeout,
            @Value("${app.viacep.read-timeout-ms}") int readTimeout) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);
        return RestClient.builder().baseUrl(baseUrl).requestFactory(factory).build();
    }
}
