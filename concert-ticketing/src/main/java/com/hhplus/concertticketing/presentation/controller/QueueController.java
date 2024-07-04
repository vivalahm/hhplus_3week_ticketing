package com.hhplus.concertticketing.presentation.controller;

import com.hhplus.concertticketing.business.service.QueueService;
import com.hhplus.concertticketing.common.exception.CustomException;
import com.hhplus.concertticketing.presentation.dto.request.QueueRequestDto;
import com.hhplus.concertticketing.presentation.dto.response.TokenResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class QueueController {
    @Autowired
    private QueueService queueService;

    @PostMapping("/token")
    public ResponseEntity<TokenResponseDto> generateToken(@RequestBody QueueRequestDto request) {
        try {
            return ResponseEntity.ok(queueService.generateToken(request));
        } catch (IllegalArgumentException e) {
            throw new CustomException("400", "Missing or invalid userId");
        } catch (Exception e) {
            throw new CustomException("500", "Internal server error");
        }
    }
}
