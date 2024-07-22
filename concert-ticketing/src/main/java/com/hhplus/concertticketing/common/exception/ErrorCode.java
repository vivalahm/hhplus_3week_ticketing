package com.hhplus.concertticketing.common.exception;

public enum ErrorCode {
    BAD_REQUEST("400", "잘못된 요청"),
    UNAUTHORIZED("401", "권한 없음"),
    NOT_FOUND("404", "찾을 수 없음"),
    INTERNAL_SERVER_ERROR("500", "내부 서버 오류");

    private final String code;
    private final String description;

    ErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}