package io.hhplus.tdd.integration.point;

import io.hhplus.tdd.point.component.PointManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
//@ActiveProfiles("sync")
//@ActiveProfiles("lock")
@ActiveProfiles("exec")
class PointManagerTest {

    @Autowired
    private PointManager pointManager;

    @Test
    @DisplayName("Race Condition 으로 원치 않는 결과를 만드는 테스트 (비교용)")
    void notRunSequentialExecution() throws InterruptedException {
        // given
        final int CHARGE_POINT = 100;
        final int JOB_COUNT = 20;
        int[] amount = {0};
        final CountDownLatch latch = new CountDownLatch(JOB_COUNT);
        ExecutorService executor = Executors.newFixedThreadPool(20);

        // when
        for(int currentJob = 0; currentJob < JOB_COUNT; currentJob++) {
            executor.submit(() -> {
                try {
                    int gotAmount = amount[0];
                    Thread.sleep(200);
                    latch.countDown();
                    amount[0] = gotAmount + CHARGE_POINT;
                } catch (InterruptedException e) {}
            });
        }

        latch.await();

        // then
        assertEquals(0, latch.getCount());
        assertNotEquals(CHARGE_POINT * JOB_COUNT, amount[0]);
    }

    @Test
    @DisplayName("유저 순차적 실행 보장 테스트")
    void pointTestForSingleUser() throws Exception {
        // given
        final int CHARGE_POINT = 100;
        final int JOB_COUNT = 20;
        int[] amount = {0};
        final CountDownLatch latch = new CountDownLatch(JOB_COUNT);
        ExecutorService executor = Executors.newFixedThreadPool(20);

        // when
        for(int currentJob = 0; currentJob < JOB_COUNT; currentJob++) {
            executor.submit(() -> {
                try {
                    pointManager.changePoint(1L, () -> {
                        int gotAmount = amount[0];
                        Thread.sleep(200);
                        latch.countDown();
                        amount[0] = gotAmount + CHARGE_POINT;

                        return null;
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }

        latch.await();

        // then
        assertEquals(0, latch.getCount());
        assertEquals(CHARGE_POINT * JOB_COUNT, amount[0]);
    }
}