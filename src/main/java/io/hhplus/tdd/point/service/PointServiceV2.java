package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.TransactionType;
import io.hhplus.tdd.point.domain.Member;
import io.hhplus.tdd.point.domain.Point;
import io.hhplus.tdd.point.dto.PointHistory;
import io.hhplus.tdd.point.dto.UserPoint;
import io.hhplus.tdd.point.exception.PointLockTimeoutException;
import io.hhplus.tdd.point.exception.PointProcessingInterruptedException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PointServiceV2 {

    private final PointHistoryTable pointHistoryTable;
    private final UserPointTable userPointTable;
    private final Map<Long, ReentrantLock> userLocks = new ConcurrentHashMap<>();

    public UserPoint charge(long id, long amount) {
        ReentrantLock lock = userLocks.computeIfAbsent(id, key -> new ReentrantLock());
        boolean lockAcquired = false;
        UserPoint originalPoint = null;
        int originalHistorySize = 0;

        try {
            lockAcquired = lock.tryLock(1, TimeUnit.SECONDS);

            if (!lockAcquired) {
                throw new PointLockTimeoutException();
            }

            originalPoint = userPointTable.selectById(id);
            originalHistorySize = pointHistoryTable.selectAllByUserId(id).size();

            Point point = new Point(new Member(originalPoint.id()), originalPoint.point());
            long chargedPoint = point.charge(amount);

            UserPoint updatedUserPoint = userPointTable.insertOrUpdate(id, chargedPoint);
            pointHistoryTable.insert(id, amount, TransactionType.CHARGE, System.currentTimeMillis());

            return updatedUserPoint;

        } catch (InterruptedException e) {
            rollbackCharge(id, originalPoint, originalHistorySize);
            Thread.currentThread().interrupt();
            throw new PointProcessingInterruptedException(e);
        } finally {
            if (lockAcquired) {
                lock.unlock();
            }
        }
    }

    private void rollbackCharge(long id, UserPoint originalPoint, int originalHistorySize) {
        try {
            userPointTable.insertOrUpdate(id, originalPoint.point());

            List<PointHistory> currentHistories = pointHistoryTable.selectAllByUserId(id);
            int currentHistorySize = currentHistories.size();

            if (currentHistorySize > originalHistorySize) {
                pointHistoryTable.insert(id, currentHistories.get(currentHistorySize - 1).amount(),
                        TransactionType.USE, System.currentTimeMillis());
            }

        } catch (Exception rollbackException) {
            log.error("롤백 실패! userId={}", id, rollbackException);
        }
    }

    public UserPoint use(long id, long amount) {
        ReentrantLock lock = userLocks.computeIfAbsent(id, key -> new ReentrantLock());
        boolean lockAcquired = false;
        UserPoint originalPoint = null;
        int originalHistorySize = 0;

        try {
            lockAcquired = lock.tryLock(1, TimeUnit.SECONDS);

            if (!lockAcquired) {
                throw new PointLockTimeoutException();
            }

            originalPoint = userPointTable.selectById(id);
            originalHistorySize = pointHistoryTable.selectAllByUserId(id).size();

            Point point = new Point(new Member(originalPoint.id()), originalPoint.point());
            long usedPoint = point.use(amount);

            UserPoint updatedUserPoint = userPointTable.insertOrUpdate(id, usedPoint);
            pointHistoryTable.insert(id, amount, TransactionType.USE, System.currentTimeMillis());

            return updatedUserPoint;

        } catch (InterruptedException e) {
            if (lockAcquired) {
                rollbackUse(id, originalPoint, originalHistorySize);
            }
            Thread.currentThread().interrupt();
            throw new PointProcessingInterruptedException(e);

        } finally {
            if (lockAcquired) {
                lock.unlock();
            }
        }
    }

    private void rollbackUse(long id, UserPoint originalPoint, int originalHistorySize) {
        try {
            userPointTable.insertOrUpdate(id, originalPoint.point());

            List<PointHistory> currentHistories = pointHistoryTable.selectAllByUserId(id);
            int currentHistorySize = currentHistories.size();

            if (currentHistorySize > originalHistorySize) {
                pointHistoryTable.insert(id, currentHistories.get(currentHistorySize - 1).amount(),
                        TransactionType.CHARGE, System.currentTimeMillis());
            }

        } catch (Exception rollbackException) {
            log.error("롤백 실패 userId={}", id, rollbackException);
        }
    }

    public UserPoint getPoint(long id) {
        return userPointTable.selectById(id);
    }

    public List<PointHistory> getHistory(long id) {
        return pointHistoryTable.selectAllByUserId(id);
    }

}
