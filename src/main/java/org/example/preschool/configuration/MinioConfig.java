package org.example.preschool.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;
import java.time.Duration;

@Configuration
public class MinioConfig {
    @Value("${storage.minio.access-key}")
    String accessKey;
    @Value("${storage.minio.secret-key}")
    String secretKey;
    @Value("${storage.minio.internal-endpoint}")
    String internalEndpoint;
    @Value("${storage.minio.public-endpoint}")
    String publicEndpoint;
    @Value("${storage.minio.region}")
    String region;

    @Bean
    public StaticCredentialsProvider minioCredentials(
    ) {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(
                accessKey,
                secretKey
        );

        return StaticCredentialsProvider.create(credentials);
    }

    @Bean
    public S3Client s3Client(
            StaticCredentialsProvider credentialsProvider
    ) {
        return S3Client.builder()
                .endpointOverride(
                        URI.create(internalEndpoint)
                )
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider)
                .httpClientBuilder(
                        UrlConnectionHttpClient.builder()
                                .connectionTimeout(
                                        Duration.ofSeconds(10)
                                )
                                .socketTimeout(
                                        Duration.ofSeconds(60)
                                )
                )
                .serviceConfiguration(
                        S3Configuration.builder()
                                .pathStyleAccessEnabled(true)
                                .build()
                )
                .build();
    }

    @Bean
    public S3Presigner s3Presigner(
            StaticCredentialsProvider credentialsProvider
    ) {
        return S3Presigner.builder()
                .endpointOverride(
                        URI.create(publicEndpoint)
                )
                .region(Region.of(region))
                .credentialsProvider(credentialsProvider)
                .serviceConfiguration(
                        S3Configuration.builder()
                                .pathStyleAccessEnabled(true)
                                .build()
                )
                .build();
    }
}