package io.hhplus.tdd.point.component;

import io.hhplus.tdd.point.dto.UserPoint;
import org.springframework.context.annotation.Profile;

import java.util.concurrent.Callable;

@Profile("sync")
public interface PointManager {
    public UserPoint changePoint(Long id, Callable<UserPoint> task) throws Exception;
}