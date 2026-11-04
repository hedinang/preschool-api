package org.example.preschool.configuration;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.TrustAllStrategy;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;

@Configuration
public class RestTemplateConfig {
    @Value("${internal-key}")
    String internalKey;

    @Bean
    public RestTemplate restTemplate() throws Exception {

        SSLContext sslContext = SSLContextBuilder.create()
                .loadTrustMaterial(null, TrustAllStrategy.INSTANCE)
                .build();

        SSLConnectionSocketFactory sslSocketFactory =
                new SSLConnectionSocketFactory(
                        sslContext,
                        NoopHostnameVerifier.INSTANCE
                );

        PoolingHttpClientConnectionManager connectionManager =
                PoolingHttpClientConnectionManagerBuilder.create()
                        .setSSLSocketFactory(sslSocketFactory)
                        .build();

        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();

        HttpComponentsClientHttpRequestFactory factory =
                new HttpComponentsClientHttpRequestFactory(httpClient);

        factory.setConnectTimeout(20000);

        RestTemplate restTemplate = new RestTemplate(factory);

        restTemplate.getInterceptors().add((request, body, execution) -> {
            request.getHeaders().set(
                    "internalKey",
                    internalKey
            );

            return execution.execute(request, body);
        });

        return restTemplate;
    }
}