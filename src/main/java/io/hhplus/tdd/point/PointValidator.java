package io.hhplus.tdd.point;

import org.springframework.stereotype.Component;

import static io.hhplus.tdd.point.PointException.PointError.*;

@Component
public class PointValidator {

    private PointValidator(){}

    private static final long MAX_POINT = 10_000L;
    private static final long MIN_POINT = 0L;

    public static void validatePoint(final long inputPoint, final long afterPoint) throws PointException {
        if(inputPoint <= 0L) {
            throw new PointException(NOT_POSITIVE);
        }

        if(afterPoint < MIN_POINT) {
            throw new PointException(NOT_ENOUGH_POINT);
        }

        if(MAX_POINT < afterPoint) {
            throw new PointException(POINT_OVERFLOW);
        }
    }
}