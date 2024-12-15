package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static io.hhplus.tdd.point.TransactionType.CHARGE;
import static io.hhplus.tdd.point.TransactionType.USE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    private final long USER_ID = 1L;
    private final long BASE_POINT = 1000L;

    @Mock
    private UserPointTable pointTable;

    @Mock
    private PointHistoryTable historyTable;

    @Test
    @DisplayName("유저 포인트 조회")
    void findPointById() {
        // given
        when(pointTable.selectById(USER_ID)).thenReturn(new UserPoint(1L, BASE_POINT, System.currentTimeMillis()));

        // when
        UserPoint userPoint = pointTable.selectById(1L);

        // then
        assertAll(
                () -> assertEquals(USER_ID, userPoint.id()),
                () -> assertEquals(BASE_POINT, userPoint.point())
        );
    }

    @Test
    @DisplayName("유저 데이터 삽입")
    void saveUserPoint() {
        // given
        when(pointTable.insertOrUpdate(USER_ID, BASE_POINT)).thenReturn(new UserPoint(1L, BASE_POINT, System.currentTimeMillis()));

        // when
        UserPoint userPoint = pointTable.insertOrUpdate(USER_ID, BASE_POINT);

        // then
        assertAll(
                () -> assertEquals(USER_ID, userPoint.id()),
                () -> assertEquals(BASE_POINT, userPoint.point())
        );
    }

    @Test
    @DisplayName("유저 포인트 이용 내역 조회")
    void findHistoriesById() {
        // given
        when(historyTable.selectAllByUserId(USER_ID)).thenReturn(List.of(
                new PointHistory(1L, USER_ID, 1000L, CHARGE, System.currentTimeMillis()),
                new PointHistory(2L, USER_ID, 3000L, CHARGE, System.currentTimeMillis()),
                new PointHistory(3L, USER_ID, 2000L, USE, System.currentTimeMillis())
        ));

        // when
        List<PointHistory> histories = historyTable.selectAllByUserId(USER_ID);

        // then
        assertAll(
                () -> assertEquals(3, histories.size()),
                () -> assertEquals(2, histories.stream().filter(e -> e.type() == CHARGE).count()),
                () -> assertEquals(1, histories.stream().filter(e -> e.type() == USE).count())
        );
    }

    @Test
    @DisplayName("유저 포인터 충전")
    void chargeUserPoint() {
        // given
        final long AFTER_CHARGE = 2000L;
        when(pointTable.insertOrUpdate(1L, AFTER_CHARGE)).thenReturn(new UserPoint(1L, AFTER_CHARGE, System.currentTimeMillis()));

        // when
        UserPoint userPoint = pointTable.insertOrUpdate(1L, AFTER_CHARGE);

        // then
        assertAll(
                () -> assertEquals(USER_ID, userPoint.id()),
                () -> assertEquals(AFTER_CHARGE, userPoint.point())
        );
    }

    @Test
    @DisplayName("유저 포인트 사용")
    void useUserPoint() {
        // given
        final long AFTER_CHARGE = 2000L;
        when(pointTable.insertOrUpdate(1L, AFTER_CHARGE)).thenReturn(new UserPoint(1L, AFTER_CHARGE, System.currentTimeMillis()));

        // when
        UserPoint userPoint = pointTable.insertOrUpdate(1L, AFTER_CHARGE);

        // then
        assertAll(
                () -> assertEquals(USER_ID, userPoint.id()),
                () -> assertEquals(AFTER_CHARGE, userPoint.point())
        );
    }
}