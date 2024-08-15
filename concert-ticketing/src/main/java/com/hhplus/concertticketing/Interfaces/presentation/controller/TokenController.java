package com.hhplus.concertticketing.Interfaces.presentation.controller;

import com.hhplus.concertticketing.application.usecase.TokenUseCase;
import com.hhplus.concertticketing.common.exception.CustomException;
import com.hhplus.concertticketing.common.exception.ErrorCode;
import com.hhplus.concertticketing.Interfaces.presentation.dto.request.TokenRequest;
import com.hhplus.concertticketing.Interfaces.presentation.dto.response.TokenResponse;
import com.hhplus.concertticketing.Interfaces.presentation.dto.response.TokenStatusResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/token")
@Tag(name = "토큰 컨트롤러", description = "토큰 관리를 위한 API")
public class TokenController {
    @Autowired
    private TokenUseCase tokenUseCase;

    @PostMapping("/issue")
    @Operation(summary = "토큰 생성", description = "사용자를 위한 새로운 토큰을 발급합니다.")
    public ResponseEntity<TokenResponse> generateToken(
            @RequestBody @Parameter(description = "사용자 ID를 포함한 TokenRequest 객체") TokenRequest request) {
        try {
            return ResponseEntity.ok(tokenUseCase.issueToken(request, 30));
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/status")
    @Operation(summary = "토큰 상태 확인", description = "주어진 토큰의 상태를 반환합니다.")
    public ResponseEntity<TokenStatusResponse> getTokenStatus(HttpServletRequest request) {
        // 요청 헤더에서 Authorization 값을 가져옴
        String tokenValue = request.getHeader("Authorization");
        try {
            return ResponseEntity.ok(tokenUseCase.getTokenStatus(tokenValue));
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}