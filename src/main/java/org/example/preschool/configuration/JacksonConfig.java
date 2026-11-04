package org.example.preschool.configuration;

import com.fasterxml.jackson.core.StreamReadConstraints;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class JacksonConfig {
    @Bean
    Jackson2ObjectMapperBuilderCustomizer customStreamReadConstraints() {
        return (builder) -> builder.postConfigurer((objectMapper) -> objectMapper.getFactory()
                .setStreamReadConstraints(StreamReadConstraints.builder().maxStringLength(500000000).build()));
    }
}
