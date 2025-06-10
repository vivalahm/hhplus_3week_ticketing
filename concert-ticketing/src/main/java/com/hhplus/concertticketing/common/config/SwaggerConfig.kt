package com.hhplus.concertticketing.common.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Swagger 설정
 */
@Configuration
class SwaggerConfig {
    @Bean
    fun openAPI(): OpenAPI = OpenAPI()
        .components(Components())
        .info(apiInfo())

    private fun apiInfo(): Info = Info()
        .title("콘서트 예약 시스템") // API의 제목
        .description("항해플러스 콘서트 예약 시스템") // API 설명
        .version("1.0.0") // API 버전
}
