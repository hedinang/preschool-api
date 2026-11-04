package org.example.preschool.configuration;

import lombok.Data;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@Data
public class ThreadPoolConfig {
    private ThreadPoolTaskExecutor messageResponseThreadPool;
    private ThreadPoolTaskExecutor messageConsumerThreadPool;
    private ThreadPoolTaskExecutor messageShareConsumerThreadPool;
    private ThreadPoolTaskExecutor messageSendConsumerThreadPool;
    private ThreadPoolTaskExecutor commentConsumerThreadPool;
    private ThreadPoolTaskExecutor commentChunkThreadPool;
    private ThreadPoolTaskExecutor chunkConsumerThreadPool;
    private ThreadPoolTaskExecutor avatarConsumerThreadPool;
    private ThreadPoolTaskExecutor systemMessageThreadPool;
    private ThreadPoolTaskExecutor callingConsumerThreadPool;
    private ThreadPoolTaskExecutor initSelfChatThreadPool;
    private ThreadPoolTaskExecutor ringingThreadPool;

    public ThreadPoolConfig() {
        this.messageResponseThreadPool = setThreadPool(5, 10, "Message-Response-Pool-");
        this.messageConsumerThreadPool = setThreadPool(5, 10, "Message-Consumer-Pool-");
        this.messageShareConsumerThreadPool = setThreadPool(5, 10, "Message-Share-Consumer-Pool-");
        this.messageSendConsumerThreadPool = setThreadPool(5, 10, "Message-Send-Consumer-Pool-");
        this.commentConsumerThreadPool = setThreadPool(5, 10, "Comment-Consumer-Pool-");
        this.chunkConsumerThreadPool = setThreadPool(5, 10, "Chunk-Consumer-Pool-");
        this.avatarConsumerThreadPool = setThreadPool(5, 10, "Chunk-Consumer-Pool-");
        this.systemMessageThreadPool = setThreadPool(5, 10, "System-Message-Pool-");
        this.callingConsumerThreadPool = setThreadPool(5, 10, "Calling-Consumer-Pool-");
        this.initSelfChatThreadPool = setThreadPool(5, 10, "Init-Self-Chat-Consumer-Pool-");
        this.ringingThreadPool = setThreadPool(5, 10, "Ringing-Consumer-Pool-");
    }

    public static ThreadPoolTaskExecutor setThreadPool(int corePoolSize, int maximumPoolSize, String threadNamePrefix) {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
        threadPoolTaskExecutor.setMaxPoolSize(maximumPoolSize);
        threadPoolTaskExecutor.setQueueCapacity(1000);
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        threadPoolTaskExecutor.setThreadNamePrefix(threadNamePrefix);
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }
}
