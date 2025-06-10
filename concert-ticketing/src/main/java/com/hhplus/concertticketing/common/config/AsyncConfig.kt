package com.hhplus.concertticketing.common.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.util.concurrent.Executor

/**
 * 비동기 처리를 위한 스레드풀 설정
 */
@Configuration
@EnableAsync
class AsyncConfig {
    @Bean(name = "taskExecutor")
    fun asyncExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = 2 // 기본 스레드 수
        executor.maxPoolSize = 100 // 최대 스레드 수
        executor.setQueueCapacity(5) // 큐 용량
        executor.threadNamePrefix = "Async-" // 스레드 이름 접두사
        executor.initialize()
        return executor
    }
}
