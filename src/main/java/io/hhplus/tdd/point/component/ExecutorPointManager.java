package io.hhplus.tdd.point.component;

import io.hhplus.tdd.point.dto.UserPoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.*;

@Slf4j
@Component
@Profile("exec")
public class ExecutorPointManager implements PointManager{

    private final Map<Long, ThreadPoolExecutor> executorMap = new ConcurrentHashMap<>();

    @Override
    public UserPoint changePoint(Long id, Callable<UserPoint> callable) throws Exception {
        return executorMap.computeIfAbsent(id, key -> new ThreadPoolExecutor(1, 1, 60, TimeUnit.SECONDS, new LinkedBlockingQueue<>()))
                          .submit(callable)
                          .get();
    }
}