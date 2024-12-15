package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static io.hhplus.tdd.point.TransactionType.*;

@SpringBootTest
class SyncPointManagerTest {

    Logger log = LoggerFactory.getLogger(SyncPointManagerTest.class);

    @Autowired
    private UserPointTable pointTable;

    @Autowired
    private PointHistoryTable historyTable;

    @Autowired
    private SyncPointManager syncPointManager;

    @Test
    void test() throws Exception {
        final long USER_ID = 1L;
        final long CHARGE_POINT = 100L;
        final long JOB_COUNT = 20;

        for(int currentJob = 0; currentJob < JOB_COUNT; currentJob++) {
            syncPointManager.runTask(() -> {
                long afterPoint = pointTable.selectById(USER_ID).point() + CHARGE_POINT;
                log.info("###");

                historyTable.insert(USER_ID, afterPoint, CHARGE, System.currentTimeMillis());

                return pointTable.insertOrUpdate(USER_ID, afterPoint);
            });
        }

        System.out.println(
                pointTable.selectById(USER_ID)
        );
        System.out.println(
                historyTable.selectAllByUserId(USER_ID)
        );
    }

    @Test
    void test2() throws InterruptedException {
        final long USER_ID = 1L;
        final long CHARGE_POINT = 100L;
        final long JOB_COUNT = 200;
        final ExecutorService executor = Executors.newFixedThreadPool(50);

        for(int currentJob = 0; currentJob < JOB_COUNT; currentJob++) {
            executor.submit(() -> {
                long afterPoint = pointTable.selectById(USER_ID).point() + CHARGE_POINT;
                System.out.println("???");

                historyTable.insert(USER_ID, afterPoint, CHARGE, System.currentTimeMillis());
                System.out.println("###");

                pointTable.insertOrUpdate(USER_ID, afterPoint);
                System.out.println("$$$");
            });
        }

        executor.shutdown();

        System.out.println(
                pointTable.selectById(USER_ID)
        );
        System.out.println(
                historyTable.selectAllByUserId(USER_ID)
        );
    }
}