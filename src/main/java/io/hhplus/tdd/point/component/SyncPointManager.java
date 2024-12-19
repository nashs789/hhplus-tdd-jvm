package io.hhplus.tdd.point.component;

import io.hhplus.tdd.point.dto.UserPoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;

@Slf4j
@Component
@Profile("sync")
public class SyncPointManager implements PointManager {

    @Override
    public synchronized UserPoint changePoint(Long id, Callable<UserPoint> callable) throws Exception {
        return callable.call();
    }
}
