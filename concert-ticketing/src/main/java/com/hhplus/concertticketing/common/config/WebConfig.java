package com.hhplus.concertticketing.common.config;

import com.hhplus.concertticketing.Interfaces.TokenInterceptor;
import com.hhplus.concertticketing.common.filter.RequestResponseLoggingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private TokenInterceptor tokenInterceptor;

    @Bean
    public FilterRegistrationBean<RequestResponseLoggingFilter> loggingFilter(){
        FilterRegistrationBean<RequestResponseLoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RequestResponseLoggingFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tokenInterceptor)
                .addPathPatterns("/api/**")
                .excludePathPatterns("/api/point/**", "/api/token/issue", "/api-docs", "/swagger-ui/**, /api/*/saveConcert, /api/*/available-dates, /api/*/available-dates");
    }
}