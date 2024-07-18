package com.hhplus.concertticketing.adaptor.presentation.controller;

import com.hhplus.concertticketing.application.usecase.CustomerUseCase;
import com.hhplus.concertticketing.common.exception.CustomException;
import com.hhplus.concertticketing.common.exception.ErrorCode;
import com.hhplus.concertticketing.adaptor.presentation.dto.request.ChargePointRequest;
import com.hhplus.concertticketing.adaptor.presentation.dto.response.CustomerPointResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Tag(name = "고객 컨트롤러", description = "고객 관리를 위한 API")
public class CustomerController {
    @Autowired
    private CustomerUseCase customerUseCase;

    @PatchMapping("/point/charge")
    @Operation(summary = "고객 잔액 충전", description = "지정된 금액으로 고객의 잔액을 충전합니다")
    public ResponseEntity<CustomerPointResponse> chargeBalance(
            @RequestBody @Parameter(description = "고객 ID와 금액을 포함하는 ChargePointRequest 객체") ChargePointRequest request) {
        if (request.getCustomerId() == null || request.getAmount() == null) {
            throw new CustomException(ErrorCode.BAD_REQUEST);
        }
        try {
            CustomerPointResponse response = new CustomerPointResponse();
            response.setResult("200");
            response.setMessage("성공");
            response.setPoint(customerUseCase.chargePoint(request).getPoint());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "내부 서버 오류");
        }
    }

    @GetMapping("/point")
    @Operation(summary = "고객 잔액 조회", description = "지정된 고객의 잔액을 반환합니다")
    public ResponseEntity<CustomerPointResponse> getBalance(
            @RequestParam @Parameter(description = "고객의 ID") Long customerId) {
        if (customerId == null) {
            throw new CustomException(ErrorCode.BAD_REQUEST, "잘못된 사용자 ID");
        }
        try {
            CustomerPointResponse response = new CustomerPointResponse();
            response.setResult("200");
            response.setMessage("성공");
            response.setPoint(customerUseCase.getPoint(customerId).getPoint());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, "내부 서버 오류");
        }
    }
}