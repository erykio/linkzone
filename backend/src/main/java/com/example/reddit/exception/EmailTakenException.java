package com.example.reddit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EmailTakenException extends RuntimeException {

    public EmailTakenException(String value) {
        super("account with this email already exists: " + value);
    }
}
