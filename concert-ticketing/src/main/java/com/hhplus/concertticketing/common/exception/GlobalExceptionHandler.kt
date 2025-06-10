package com.hhplus.concertticketing.common.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * 전역 예외 처리기
 */
@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(CustomException::class)
    fun handleCustomException(ex: CustomException): ResponseEntity<ErrorResponse> {
        log.error("에러:", ex)
        val errorCode = ex.errorCode
        val errorResponse = ErrorResponse(errorCode.code, errorCode.description)
        return ResponseEntity(errorResponse, getHttpStatus(errorCode))
    }

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception): ResponseEntity<ErrorResponse> {
        log.error("에러:", ex)
        val errorResponse = ErrorResponse(
            ErrorCode.INTERNAL_SERVER_ERROR.code,
            ErrorCode.INTERNAL_SERVER_ERROR.description
        )
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    private fun getHttpStatus(errorCode: ErrorCode): HttpStatus = when (errorCode) {
        ErrorCode.BAD_REQUEST -> HttpStatus.BAD_REQUEST
        ErrorCode.NOT_FOUND -> HttpStatus.NOT_FOUND
        ErrorCode.UNAUTHORIZED -> HttpStatus.UNAUTHORIZED
        else -> HttpStatus.INTERNAL_SERVER_ERROR
    }
}
