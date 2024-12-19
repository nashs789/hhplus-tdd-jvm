package io.hhplus.tdd.point.exception;

import io.hhplus.tdd.global.exception.BankException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class UserException extends BankException {

    @Getter
    @RequiredArgsConstructor
    public enum UserError {
        NOT_EXIST_USER(BAD_REQUEST, "없는 유저 입니다.");

        private final HttpStatus status;
        private final String msg;
    }

    public UserException(UserError userError) {
        super(userError.getMsg(), userError.getStatus());
    }
}