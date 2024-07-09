package com.hhplus.concertticketing.business.service;

import com.hhplus.concertticketing.business.model.Customer;
import com.hhplus.concertticketing.business.model.Token;
import com.hhplus.concertticketing.business.repository.CustomerRepository;
import com.hhplus.concertticketing.business.repository.ReservationRepository;
import com.hhplus.concertticketing.business.repository.SeatRepository;
import com.hhplus.concertticketing.business.repository.TokenRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TokenService {
    private final TokenRepository tokenRepository;
    private final CustomerRepository customerRepository;
    private final ReservationRepository reservationRepository;
    private final SeatRepository seatRepository;

    public TokenService(TokenRepository tokenRepository, CustomerRepository customerRepository,
                        ReservationRepository reservationRepository, SeatRepository seatRepository) {
        this.tokenRepository = tokenRepository;
        this.customerRepository = customerRepository;
        this.reservationRepository = reservationRepository;
        this.seatRepository = seatRepository;
    }

    public Token issueToken(Long customerId, Long concertId, int maxActiveTokens){
        long activeTokenCount = tokenRepository.getCountActiveTokens(concertId);
        boolean hasWaitingTokens = tokenRepository.getExistWaitingTokens(concertId);

        Token token = new Token();
        Customer customer = customerRepository.getCustomerById(customerId).orElseThrow();
        token.setCustomer(customer);
        token.setConcertId(customerId);
        token.setCreatedAt(LocalDateTime.now());
        token.setExpiresAt(LocalDateTime.now().plusHours(2));

        if(activeTokenCount >= maxActiveTokens && !hasWaitingTokens){
            token.setStatus("ACTIVE");
        }
        else {
            token.setStatus("WAITING");
        }

        return tokenRepository.saveToken(token);
    }

}
