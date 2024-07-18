package com.hhplus.concertticketing.adaptor.presentation.controller;

import com.hhplus.concertticketing.application.usecase.TokenUseCase;
import com.hhplus.concertticketing.common.exception.CustomException;
import com.hhplus.concertticketing.adaptor.presentation.dto.request.TokenRequest;
import com.hhplus.concertticketing.adaptor.presentation.dto.response.TokenResponse;
import com.hhplus.concertticketing.adaptor.presentation.dto.response.TokenStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/token")
@Tag(name = "Token Controller", description = "API for managing tokens")
public class TokenController {
    @Autowired
    private TokenUseCase tokenUseCase;

    @PostMapping("/issue")
    @Operation(summary = "Generate token", description = "Issues a new token for a given user")
    public ResponseEntity<TokenResponse> generateToken(
            @RequestBody @Parameter(description = "TokenRequest object containing userId") TokenRequest request) {
        try {
            return ResponseEntity.ok(tokenUseCase.issueToken(request, 30));
        } catch (IllegalArgumentException e) {
            throw new CustomException("400", "Missing or invalid userId");
        } catch (Exception e) {
            throw new CustomException("500", "Internal server error");
        }
    }

    @GetMapping("/status")
    @Operation(summary = "Get token status", description = "Returns the status of a given token")
    public ResponseEntity<TokenStatusResponse> getTokenStatus(
            @RequestParam @Parameter(description = "Value of the token") String tokenValue) {
        try {
            return ResponseEntity.ok(tokenUseCase.getTokenStatus(tokenValue));
        } catch (IllegalArgumentException e) {
            throw new CustomException("400", "Missing or invalid userId");
        } catch (Exception e) {
            throw new CustomException("500", "Internal server error");
        }
    }
}