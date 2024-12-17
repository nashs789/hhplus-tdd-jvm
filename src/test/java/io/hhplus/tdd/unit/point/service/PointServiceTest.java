package io.hhplus.tdd.unit.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.component.SyncPointManager;
import io.hhplus.tdd.point.dto.PointHistory;
import io.hhplus.tdd.point.dto.PointRequest;
import io.hhplus.tdd.point.dto.UserPoint;
import io.hhplus.tdd.point.service.PointService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static io.hhplus.tdd.point.TransactionType.CHARGE;
import static io.hhplus.tdd.point.TransactionType.USE;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    private final long USER_ID = 1L;
    private final long BASE_POINT = 1000L;

    @Mock
    private UserPointTable pointTable;

    @Mock
    private PointHistoryTable historyTable;

    @Mock
    private SyncPointManager syncPointManager;

    @InjectMocks
    private PointService pointService;

    @Test
    @DisplayName("유저 포인트 조회")
    void findPointById() {
        // given
        when(pointTable.selectById(USER_ID)).thenReturn(new UserPoint(1L, BASE_POINT, System.currentTimeMillis()));

        // when
        UserPoint userPoint = pointService.findPointById(USER_ID);

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
        List<PointHistory> histories = pointService.findAllHistories(USER_ID);

        // then
        assertAll(
                () -> assertEquals(3, histories.size()),
                () -> assertEquals(2, histories.stream().filter(e -> e.type() == CHARGE).count()),
                () -> assertEquals(1, histories.stream().filter(e -> e.type() == USE).count())
        );
    }

    @Test
    @DisplayName("유저 포인터 충전")
    void chargeUserPoint() throws Exception {
        // given
        final long CHARGE_POINT = 1000L;
        final long AFTER_CHARGE = BASE_POINT + CHARGE_POINT;
        PointRequest pointRequest = new PointRequest(AFTER_CHARGE);

        when(syncPointManager.changePoint(any(), any())).thenReturn(new UserPoint(USER_ID, AFTER_CHARGE, System.currentTimeMillis()));

        // when
        UserPoint userPoint = pointService.chargePoint(USER_ID, pointRequest);

        // then
        assertAll(
                () -> assertEquals(USER_ID, userPoint.id()),
                () -> assertEquals(AFTER_CHARGE, userPoint.point())
        );
    }

    @Test
    @DisplayName("유저 포인트 사용")
    void useUserPoint() throws Exception {
        // given
        final long USE_POINT = 500L;
        final long AFTER_USE = BASE_POINT - USE_POINT;
        PointRequest pointRequest = new PointRequest(AFTER_USE);

        when(syncPointManager.changePoint(any(), any())).thenReturn(new UserPoint(USER_ID, AFTER_USE, System.currentTimeMillis()));

        // when
        UserPoint userPoint = pointService.usePoint(1L, pointRequest);

        // then
        assertAll(
                () -> assertEquals(USER_ID, userPoint.id()),
                () -> assertEquals(AFTER_USE, userPoint.point())
        );
    }
}