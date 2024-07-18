package com.hhplus.concertticketing.adaptor;

import com.hhplus.concertticketing.adaptor.presentation.dto.response.TokenStatusResponse;
import com.hhplus.concertticketing.application.usecase.TokenUseCase;
import com.hhplus.concertticketing.business.model.TokenStatus;
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
        logger.info("Request URI: {}", requestURI);

        if (requestURI.equals("/api/point/charge") ||
                requestURI.equals("/api/token/issue")) {
            logger.info("Request allowed without token for URI: {}", requestURI);
            return true;
        }

        // 요청 헤더에서 Authorization 값을 가져옴
        String tokenValue = request.getHeader("Authorization");
        logger.info("Authorization header: {}", tokenValue);

        if (tokenValue == null) {
            logger.warn("No Authorization header present, request unauthorized");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        } else {
            try {
                TokenStatusResponse tokenStatusResponse = tokenUseCase.getTokenStatus(tokenValue);
                logger.info("Token status: {}", tokenStatusResponse.getStatus());

                if (!tokenStatusResponse.getStatus().equals(TokenStatus.ACTIVE)) {
                    logger.warn("Token is not active, request unauthorized");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return false;
                }
                logger.info("Token is active, request authorized");
                return true;
            } catch (Exception e) {
                logger.error("Error while verifying token: {}", e.getMessage(), e);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return false;
            }
        }
    }
}