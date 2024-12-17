package io.hhplus.tdd.point.exception;

import io.hhplus.tdd.global.exception.BankException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

public class PointException extends BankException {

    @Getter
    @RequiredArgsConstructor
    public enum PointError {
        NOT_ENOUGH_POINT(BAD_REQUEST, "보유 포인트 부족"),
        POINT_OVERFLOW(BAD_REQUEST, "보유 가능한 최대 포인트 초과"),
        NOT_POSITIVE(BAD_REQUEST, "양의 포인트만 입력 가능");

        private final HttpStatus status;
        private final String msg;
    }

    public PointException(PointError pointError) {
        super(pointError.getMsg(), pointError.getStatus());
    }
}
