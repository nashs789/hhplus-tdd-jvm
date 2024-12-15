package io.hhplus.tdd.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BankException extends RuntimeException {

    private final HttpStatus status;

    public BankException(String msg, HttpStatus status) {
        super(msg);
        this.status = status;
    }
}
