package com.hhplus.concertticketing.common.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final String result;
    private final String message;

    public CustomException(String result, String message) {
        super(message);
        this.result = result;
        this.message = message;
    }
}
