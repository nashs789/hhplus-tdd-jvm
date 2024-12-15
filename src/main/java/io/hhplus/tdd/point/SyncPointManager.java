package io.hhplus.tdd.point;

import io.hhplus.tdd.point.dto.UserPoint;
import org.springframework.stereotype.Component;

import java.util.concurrent.Callable;

@Component
public class SyncPointManager {

    public synchronized UserPoint runTask(Callable<UserPoint> callable) throws Exception {
        return callable.call();
    }
}
