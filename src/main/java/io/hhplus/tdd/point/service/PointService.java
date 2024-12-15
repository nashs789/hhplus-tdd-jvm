package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.SyncPointManager;
import io.hhplus.tdd.point.dto.PointHistory;
import io.hhplus.tdd.point.dto.PointRequest;
import io.hhplus.tdd.point.dto.UserPoint;
import io.hhplus.tdd.point.validator.PointValidator;
import io.hhplus.tdd.point.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static io.hhplus.tdd.point.TransactionType.CHARGE;
import static io.hhplus.tdd.point.TransactionType.USE;

@Slf4j
@Service
@RequiredArgsConstructor
public class PointService {

    /** 유저 포인트 */
    private final UserPointTable pointTable;
    /** 유저 포인트 이력 */
    private final PointHistoryTable historyTable;

    private final SyncPointManager syncPointManager;

    public UserPoint findPointById(final long id) {
        UserValidator.validateUser(id);

        return pointTable.selectById(id);
    }

    public List<PointHistory> findAllHistories(final long id) {
        UserValidator.validateUser(id);

        return historyTable.selectAllByUserId(id);
    }

    public UserPoint chargePoint(final long id, final PointRequest pointRequest) throws Exception {
        return syncPointManager.runTask(() -> {
            UserValidator.validateUser(id);

            UserPoint userPoint = pointTable.selectById(id);
            long afterPoint = userPoint.point() + pointRequest.amount();

            PointValidator.validatePoint(pointRequest.amount(), afterPoint);

            historyTable.insert(id, afterPoint, CHARGE, pointRequest.reqTime());

            return pointTable.insertOrUpdate(id, afterPoint);
        });
    }

    public UserPoint usePoint(final long id, final PointRequest pointRequest) throws Exception {
        return syncPointManager.runTask(() -> {
            UserValidator.validateUser(id);

            UserPoint userPoint = pointTable.selectById(id);
            long afterPoint = userPoint.point() - pointRequest.amount();

            PointValidator.validatePoint(userPoint.point(), afterPoint);

            historyTable.insert(id, afterPoint, USE, pointRequest.reqTime());

            return pointTable.insertOrUpdate(id, afterPoint);
        });
    }

//    public UserPoint chargePoint(final long id, final PointRequest pointRequest) {
//        UserValidator.validateUser(id);
//
//        UserPoint userPoint = pointTable.selectById(id);
//        long afterPoint = userPoint.point() + pointRequest.amount();
//
//        PointValidator.validatePoint(userPoint.point(), afterPoint);
//
//        historyTable.insert(id, afterPoint, CHARGE, pointRequest.reqTime());
//
//        return pointTable.insertOrUpdate(id, afterPoint);
//    }
//
//    public UserPoint usePoint(final long id, final PointRequest pointRequest) {
//        UserValidator.validateUser(id);
//
//        UserPoint userPoint = pointTable.selectById(id);
//        long afterPoint = userPoint.point() - pointRequest.amount();
//
//        PointValidator.validatePoint(userPoint.point(), afterPoint);
//
//        historyTable.insert(id, afterPoint, USE, pointRequest.reqTime());
//
//        return pointTable.insertOrUpdate(id, afterPoint);
//    }
}