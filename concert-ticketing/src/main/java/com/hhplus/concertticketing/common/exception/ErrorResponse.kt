package com.hhplus.concertticketing.common.exception

/**
 * 에러 응답을 표현하는 데이터 클래스
 */
data class ErrorResponse(
    var result: String,
    var message: String
)
