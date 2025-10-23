package io.hhplus.tdd.point.aop;

import io.hhplus.tdd.point.exception.PointLockTimeoutException;
import io.hhplus.tdd.point.exception.PointProcessingInterruptedException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class PointLockAspect {

    private final Map<Long, ReentrantLock> userLocks = new ConcurrentHashMap<>();

    @Around("@annotation(pointLock) && args(userId, ..)")
    public Object handlePointLock(ProceedingJoinPoint joinPoint, PointLock pointLock, long userId) throws Throwable {
        ReentrantLock lock = userLocks.computeIfAbsent(userId, key -> new ReentrantLock());
        boolean lockAcquired = false;

        try {
            lockAcquired = lock.tryLock(pointLock.timeout(), pointLock.timeUnit());

            if (!lockAcquired) {
                throw new PointLockTimeoutException();
            }

            return joinPoint.proceed();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PointProcessingInterruptedException(e);
        } finally {
            if (lockAcquired) {
                lock.unlock();
            }
        }
    }
}