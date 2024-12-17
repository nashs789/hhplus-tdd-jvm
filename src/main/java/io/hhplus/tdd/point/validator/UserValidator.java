package io.hhplus.tdd.point.validator;

import io.hhplus.tdd.point.exception.UserException;

import static io.hhplus.tdd.point.exception.UserException.UserError.NOT_EXIST_USER;

public class UserValidator {

    private UserValidator() {}

    public static void validateUser(final long id) {
        if(id <= 0L) {
            throw new UserException(NOT_EXIST_USER);
        }
    }
}