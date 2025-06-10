package com.hhplus.concertticketing.common.filter

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * 요청과 응답을 로깅하는 필터
 */
@Component
class RequestResponseLoggingFilter : Filter {
    private val logger = LoggerFactory.getLogger(RequestResponseLoggingFilter::class.java)
    private val objectMapper = ObjectMapper()

    override fun init(filterConfig: FilterConfig?) {
        // 초기화 로직이 필요하면 여기에 추가
    }

    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse

        val startTime = System.currentTimeMillis()

        // 요청 파라미터를 JSON 문자열로 변환
        val parameters = httpRequest.parameterMap.mapValues { it.value.joinToString(",") }
        val paramsJson = objectMapper.writeValueAsString(parameters)

        // 요청 로깅
        logger.info("들어오는 요청: 메소드={}, URI={}, 파라미터={}",
            httpRequest.method,
            httpRequest.requestURI,
            paramsJson)

        // 다음 필터 또는 서블릿 실행
        chain.doFilter(request, response)

        val duration = System.currentTimeMillis() - startTime

        // 응답 로깅
        logger.info("나가는 응답: 상태={}, 소요시간={}ms", httpResponse.status, duration)
    }

    override fun destroy() {
        // 정리 작업이 필요하면 여기에 추가
    }
}
