package org.example.preschool.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskDecorator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.concurrent.Executor;

@Configuration
@Slf4j
public class AsyncConfig {
    @Bean("messageVoteAsyncThreadPool")
    public Executor messageVoteAsyncThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("handle-service-async-thread-");

        executor.setTaskDecorator(new SecurityContextTaskDecorator());

        executor.initialize();
        return executor;
    }

    @Bean("restTemplateAsyncThreadPool")
    public Executor restTemplateAsyncThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("rest-template-async-thread-");

        executor.setTaskDecorator(new SecurityContextTaskDecorator());

        executor.initialize();
        return executor;
    }

    @Bean("queryAsyncThreadPool")
    public Executor queryAsyncThreadPool() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("query-async-thread-");

        executor.setTaskDecorator(new SecurityContextTaskDecorator());

        executor.initialize();
        return executor;
    }

    private static class SecurityContextTaskDecorator implements TaskDecorator {
        @Override
        public Runnable decorate(Runnable runnable) {
            SecurityContext securityContext = SecurityContextHolder.getContext();

            return () -> {
                try {
                    SecurityContextHolder.setContext(securityContext);
                    runnable.run();
                } finally {
                    SecurityContextHolder.clearContext();
                }
            };
        }
    }
}
