package com.hhplus.concertticketing.application.usecase;

import com.hhplus.concertticketing.business.model.*;
import com.hhplus.concertticketing.business.repository.*;
import com.hhplus.concertticketing.adaptor.presentation.dto.request.TokenRequest;
import com.hhplus.concertticketing.adaptor.presentation.dto.response.TokenResponse;
import com.hhplus.concertticketing.adaptor.presentation.dto.response.TokenStatusResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class TokenUseCaseIntegrationTest {

    @Autowired
    private TokenUseCase tokenUseCase;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ConcertOptionRepository concertOptionRepository;

    private Customer customer;
    private ConcertOption concertOption;

    @BeforeEach
    void setUp() {
        // Initialize Customer
        customer = new Customer();
        customer.setName("John Doe");
        customer.setPoint(100.0);
        customer = customerRepository.saveCustomer(customer);

        // Initialize ConcertOption
        concertOption = new ConcertOption();
        concertOption.setConcertId(1L);
        concertOption.setConcertDate(LocalDateTime.now().plusDays(1));
        concertOption.setIsAvailable(true);
        concertOption.setPrice(50.0);
        concertOption = concertOptionRepository.saveConcertOption(concertOption);
    }

    @Test
    @DisplayName("토큰 발급 테스트")
    void issueToken_ShouldSucceed() {
        TokenRequest request = new TokenRequest();
        request.setCustomerId(customer.getId());
        request.setConcertId(concertOption.getConcertId());

        TokenResponse response = tokenUseCase.issueToken(request, 10);

        assertNotNull(response);
        assertEquals(TokenStatus.ACTIVE, response.getStatus());

        Token token = tokenRepository.getTokenByTokenValue(response.getTokenValue()).orElseThrow();
        assertEquals(TokenStatus.ACTIVE, token.getStatus());
    }

    @Test
    @DisplayName("만료된 토큰 확인 및 업데이트 테스트")
    void checkAndUpdateExpiredTokens_ShouldUpdateExpiredTokens() {
        Token token = new Token();
        token.setConcertId(concertOption.getConcertId());
        token.setCustomerId(customer.getId());
        token.setTokenValue("token-value");
        token.setCreatedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().minusMinutes(1));
        token.setStatus(TokenStatus.ACTIVE);
        token = tokenRepository.saveToken(token);

        tokenUseCase.checkAndUpdateExpiredTokens();

        Token updatedToken = tokenRepository.getTokenById(token.getId()).orElseThrow();
        assertEquals(TokenStatus.EXPIRED, updatedToken.getStatus());
    }

    @Test
    @DisplayName("토큰 상태 확인 테스트")
    void getTokenStatus_ShouldReturnCorrectStatus() {
        Token token = new Token();
        token.setConcertId(concertOption.getConcertId());
        token.setCustomerId(customer.getId());
        token.setTokenValue("token-value");
        token.setCreatedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        token.setStatus(TokenStatus.ACTIVE);
        token = tokenRepository.saveToken(token);

        TokenStatusResponse response = tokenUseCase.getTokenStatus(token.getTokenValue());

        assertNotNull(response);
        assertEquals(TokenStatus.ACTIVE, response.getStatus());
        assertEquals(1L, response.getPosition());
    }
}
