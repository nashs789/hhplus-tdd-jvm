package io.hhplus.tdd.unit.point.validator;

import io.hhplus.tdd.point.exception.UserException;
import io.hhplus.tdd.point.validator.UserValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertThrows;

class UserValidatorTest {

    @DisplayName("유효 하지 않는 유저 번호 테스트")
    @ParameterizedTest(name = "{index} 번 유저 테스트 파라미터[{0}]")
    @ValueSource(longs = {0L, -1L, -100_000L, Long.MIN_VALUE})
    void invalidUserNumber(final long id) {
        assertThrows(UserException.class, () -> UserValidator.validateUser(id));
    }
}