package com.hhplus.concertticketing.common.exception

/**
 * API 오류 코드 정의
 */
enum class ErrorCode(val code: String, val description: String) {
    BAD_REQUEST("400", "잘못된 요청"),
    UNAUTHORIZED("401", "권한 없음"),
    NOT_FOUND("404", "찾을 수 없음"),
    INTERNAL_SERVER_ERROR("500", "내부 서버 오류")
}
