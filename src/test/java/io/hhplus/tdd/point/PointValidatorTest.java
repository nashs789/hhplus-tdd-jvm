package io.hhplus.tdd.point;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertThrows;

class PointValidatorTest {

    @DisplayName("입력 금액은 양수만 가능")
    @ParameterizedTest(name = "{index} 번 음수 테스트 파라미터[{0}]")
    @ValueSource(longs = {0L, -1L, -100_000L, Long.MIN_VALUE})
    void mustBeGreaterThanZero(final long inputPoint) {
        assertThrows(PointException.class, () -> PointValidator.validatePoint(inputPoint, 0L));
    }

    @DisplayName("포인트 부족")
    @ParameterizedTest(name = "{index} 번 포인트 부족 테스트 파라미터[{0}]")
    @ValueSource(longs = {-1L, -100_000L, Long.MIN_VALUE})
    void shortageOfPoint(final long afterPoint) {
        assertThrows(PointException.class, () -> PointValidator.validatePoint(1L, afterPoint));
    }

    @DisplayName("최대 포인트 초과")
    @ParameterizedTest(name = "{index} 번 최대 포인트 초과 테스트 파라미터[{0}]")
    @ValueSource(longs = {10_001L, 20_000L, 100_000_000L, Long.MAX_VALUE})
    void test(final long afterPoint) {
        assertThrows(PointException.class, () -> PointValidator.validatePoint(1L, afterPoint));
    }
}