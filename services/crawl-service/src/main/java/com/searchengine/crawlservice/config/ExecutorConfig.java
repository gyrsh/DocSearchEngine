package com.searchengine.crawlservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class ExecutorConfig {

    @Bean("subscriptionTaskExecutor")
    public ThreadPoolTaskExecutor subscriptionTaskExecutor(
            @Value("${crawler.thread-pools.subscription.core-size:2}") int corePoolSize,
            @Value("${crawler.thread-pools.subscription.max-size:4}") int maxPoolSize,
            @Value("${crawler.thread-pools.subscription.queue-capacity:50}") int queueCapacity) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("subscription-thread-");
        executor.initialize();
        return executor;
    }

    @Bean("trafficTaskExecutor")
    public ThreadPoolTaskExecutor trafficTaskExecutor(
            @Value("${crawler.thread-pools.traffic.core-size:10}") int corePoolSize,
            @Value("${crawler.thread-pools.traffic.max-size:20}") int maxPoolSize,
            @Value("${crawler.thread-pools.traffic.queue-capacity:200}") int queueCapacity) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("traffic-thread-");
        executor.initialize();
        return executor;
    }

    @Bean("registryTaskExecutor")
    public ThreadPoolTaskExecutor registryTaskExecutor(
            @Value("${crawler.thread-pools.registry.core-size:1}") int corePoolSize,
            @Value("${crawler.thread-pools.registry.max-size:2}") int maxPoolSize,
            @Value("${crawler.thread-pools.registry.queue-capacity:50}") int queueCapacity) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("registry-thread-");
        executor.initialize();
        return executor;
    }
}
