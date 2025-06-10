package com.hhplus.concertticketing

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.scheduling.annotation.EnableScheduling

// 메인 애플리케이션 클래스
@EnableScheduling
@EnableCaching
@SpringBootApplication
class ConcertTicketingApplication

fun main(args: Array<String>) {
    // 스프링 부트를 실행하는 진입점
    runApplication<ConcertTicketingApplication>(*args)
}
