package org.yun.service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BalanceInsufficientException extends RuntimeException {
    
    public BalanceInsufficientException(String message) {
        super(message);
    }
}



