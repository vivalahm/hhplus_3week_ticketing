package com.hhplus.concertticketing.common.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class RequestResponseLoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // 초기화 로직이 필요하면 여기에 추가
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        long startTime = System.currentTimeMillis();

        // 요청 파라미터를 문자열로 변환
        Map<String, String> parameters = httpRequest.getParameterMap().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> String.join(",", entry.getValue())
                ));
        String paramsJson = objectMapper.writeValueAsString(parameters);

        // 요청 로깅
        logger.info("들어오는 요청: 메소드={}, URI={}, 파라미터={}",
                httpRequest.getMethod(),
                httpRequest.getRequestURI(),
                paramsJson);

        // 다음 필터 또는 서블릿을 호출하기 전에 로깅을 남김
        chain.doFilter(request, response);

        long duration = System.currentTimeMillis() - startTime;

        // 응답 로깅
        logger.info("나가는 응답: 상태={}, 소요시간={}ms",
                httpResponse.getStatus(),
                duration);
    }

    @Override
    public void destroy() {
        // 정리 작업이 필요하면 여기에 추가
    }
}