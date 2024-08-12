package com.hhplus.concertticketing.application.usecase.event;

import com.hhplus.concertticketing.business.model.Token;
import org.springframework.context.ApplicationEvent;

public class TokenExpireEvent  extends ApplicationEvent {

    private final Token token;

    public TokenExpireEvent(Object source, Token token) {
        super(source);
        this.token = token;
    }

    public Token getToken() {
        return token;
    }
}
