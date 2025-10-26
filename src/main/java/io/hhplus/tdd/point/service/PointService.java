package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.TransactionType;
import io.hhplus.tdd.point.domain.Member;
import io.hhplus.tdd.point.domain.Point;
import io.hhplus.tdd.point.dto.PointHistory;
import io.hhplus.tdd.point.dto.UserPoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointHistoryTable pointHistoryTable;
    private final UserPointTable userPointTable;
    private final Map<Long, ReentrantLock> userLocks = new ConcurrentHashMap<>();

    public UserPoint charge(long id, long amount) {
        ReentrantLock lock = userLocks.computeIfAbsent(id, key -> new ReentrantLock());

        lock.lock();
        try {
            UserPoint userPoint = userPointTable.selectById(id);
            Point point = new Point(new Member(userPoint.id()), userPoint.point());
            long chargedPoint = point.charge(amount);
            UserPoint updatedUserPoint = userPointTable.insertOrUpdate(id, chargedPoint);
            pointHistoryTable.insert(id, amount, TransactionType.CHARGE, System.currentTimeMillis());
            return updatedUserPoint;
        } finally {
            lock.unlock();
        }
    }

    public UserPoint use(long id, long amount) {
        ReentrantLock lock = userLocks.computeIfAbsent(id, key -> new ReentrantLock());

        lock.lock();
        try {
            UserPoint userPoint = userPointTable.selectById(id);
            Point point = new Point(new Member(userPoint.id()), userPoint.point());
            long usedPoint = point.use(amount);
            UserPoint updatedUserPoint = userPointTable.insertOrUpdate(id, usedPoint);
            pointHistoryTable.insert(id, amount, TransactionType.USE, System.currentTimeMillis());
            return updatedUserPoint;
        } finally {
            lock.unlock();
        }
    }

    public UserPoint getPoint(long id) {
        return userPointTable.selectById(id);
    }

    public List<PointHistory> getHistory(long id) {
        return pointHistoryTable.selectAllByUserId(id);
    }

}
