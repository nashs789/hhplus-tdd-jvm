package io.hhplus.tdd.point.component;

import io.hhplus.tdd.point.dto.UserPoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
@Profile("lock")
public class LockPointManager implements PointManager {

    private final ConcurrentHashMap<Long, ReentrantLock> lockMap = new ConcurrentHashMap<>();

    @Override
    public UserPoint changePoint(Long id, Callable<UserPoint> callable) throws Exception {
        Lock lock = lockMap.computeIfAbsent(id, key -> new ReentrantLock(true));
        log.info("Lock run id[{}]", id);

        try {
            lock.lock();

            return callable.call();
        } finally {
            lock.unlock();
        }
    }
}