package com.hhplus.concertticketing.common.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException ex) {
        log.error("에러:", ex);
       switch (ex.getResult()){
           case "400":
               log.warn("400 에러 발생: {}", ex.getMessage());
               return new ResponseEntity<>(new ErrorResponse(ex.getResult(), ex.getMessage()), HttpStatus.BAD_REQUEST);
           case "404":
               return new ResponseEntity<>(new ErrorResponse(ex.getResult(), ex.getMessage()), HttpStatus.NOT_FOUND);
           default:
               return new ResponseEntity<>(new ErrorResponse(ex.getResult(), ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("에러:", ex);
        ErrorResponse errorResponse = new ErrorResponse("500", "Internal server error");
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

@Getter
@Setter
class ErrorResponse {
    private String result;
    private String message;

    public ErrorResponse(String result, String message) {
        this.result = result;
        this.message = message;
    }

}