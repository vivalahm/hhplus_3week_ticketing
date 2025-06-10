package com.hhplus.concertticketing.common.exception

/**
 * 서비스 전반에서 사용되는 커스텀 예외
 */
class CustomException(
    val errorCode: ErrorCode,
    message: String? = null
) : RuntimeException(message ?: errorCode.code)
