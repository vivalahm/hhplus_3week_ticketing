package com.hhplus.concertticketing.adaptor;

import com.hhplus.concertticketing.adaptor.presentation.dto.response.TokenStatusResponse;
import com.hhplus.concertticketing.application.usecase.TokenUseCase;
import com.hhplus.concertticketing.business.model.TokenStatus;
import com.hhplus.concertticketing.common.exception.CustomException;
import com.hhplus.concertticketing.common.exception.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class TokenInterceptor implements HandlerInterceptor {
    private final TokenUseCase tokenUseCase;
    private static final Logger logger = LoggerFactory.getLogger(TokenInterceptor.class);

    public TokenInterceptor(TokenUseCase tokenUseCase) {
        this.tokenUseCase = tokenUseCase;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        logger.info("요청 URI: {}", requestURI);

        if (requestURI.equals("/api/point/charge") ||
                requestURI.equals("/api/point") ||
                requestURI.equals("/api/token/issue")) {
            logger.info("URI에 대한 토큰 없이 요청 허용: {}", requestURI);
            return true;
        }

        // 요청 헤더에서 Authorization 값을 가져옴
        String tokenValue = request.getHeader("Authorization");
        logger.info("Authorization 헤더: {}", tokenValue);

        if (tokenValue == null) {
            logger.warn("Authorization 헤더가 존재하지 않음, 요청 권한 없음");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        } else {
            try {
                TokenStatusResponse tokenStatusResponse = tokenUseCase.getTokenStatus(tokenValue);
                logger.info("토큰 상태: {}", tokenStatusResponse.getStatus());

                if (!tokenStatusResponse.getStatus().equals(TokenStatus.ACTIVE)) {
                    logger.warn("토큰이 활성 상태가 아님, 요청 권한 없음");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    throw new CustomException(ErrorCode.UNAUTHORIZED);
                }
                logger.info("토큰이 활성 상태, 요청 권한 있음");
                return true;
            } catch (Exception e) {
                logger.error("토큰 검증 중 오류: {}", e.getMessage(), e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                throw new CustomException(ErrorCode.UNAUTHORIZED, e.getMessage());
            }
        }
    }
}