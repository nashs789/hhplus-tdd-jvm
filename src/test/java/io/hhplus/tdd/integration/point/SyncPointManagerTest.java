package io.hhplus.tdd.integration.point;

import io.hhplus.tdd.point.SyncPointManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

@SpringBootTest
class SyncPointManagerTest {

    @Autowired
    private SyncPointManager syncPointManager;

    @Test
    @DisplayName("순차적으로 실행하지 않은 테스트(비교용)")
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
    @DisplayName("순차적 실행 보장 synchronized 테스트")
    void runSequentialExecutionBySynchronized() throws Exception {
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
                    syncPointManager.runTask(() -> {
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