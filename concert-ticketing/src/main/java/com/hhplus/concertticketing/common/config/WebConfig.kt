package com.hhplus.concertticketing.common.config

import com.hhplus.concertticketing.Interfaces.TokenInterceptor
import com.hhplus.concertticketing.common.filter.RequestResponseLoggingFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * 웹 관련 설정
 */
@Configuration
class WebConfig @Autowired constructor(
    private val tokenInterceptor: TokenInterceptor
) : WebMvcConfigurer {

    @Bean
    fun loggingFilter(): FilterRegistrationBean<RequestResponseLoggingFilter> {
        val registrationBean = FilterRegistrationBean(RequestResponseLoggingFilter())
        registrationBean.addUrlPatterns("/*")
        return registrationBean
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(tokenInterceptor)
            .addPathPatterns("/api/**")
            .excludePathPatterns(
                "/api/point/**",
                "/api/token/issue",
                "/api-docs",
                "/swagger-ui/**, /api/*/saveConcert, /api/*/available-dates, /api/*/available-dates"
            )
    }
}
