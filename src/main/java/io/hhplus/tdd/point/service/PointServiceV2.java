package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.TransactionType;
import io.hhplus.tdd.point.aop.PointLock;
import io.hhplus.tdd.point.domain.Member;
import io.hhplus.tdd.point.domain.Point;
import io.hhplus.tdd.point.dto.PointHistory;
import io.hhplus.tdd.point.dto.UserPoint;
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
    private final PointRollbackHandler rollbackHandler;

    @PointLock
    public UserPoint charge(long id, long amount) {
        UserPoint originalPoint = userPointTable.selectById(id);
        int originalHistorySize = pointHistoryTable.selectAllByUserId(id).size();

        try {
            Point point = new Point(new Member(originalPoint.id()), originalPoint.point());
            long chargedPoint = point.charge(amount);

            UserPoint updatedUserPoint = userPointTable.insertOrUpdate(id, chargedPoint);
            pointHistoryTable.insert(id, amount, TransactionType.CHARGE, System.currentTimeMillis());

            return updatedUserPoint;

        } catch (Exception e) {
            rollbackHandler.rollbackCharge(id, originalPoint, originalHistorySize);
            throw e;
        }
    }

    @PointLock
    public UserPoint use(long id, long amount) {
        UserPoint originalPoint = userPointTable.selectById(id);
        int originalHistorySize = pointHistoryTable.selectAllByUserId(id).size();

        try {
            Point point = new Point(new Member(originalPoint.id()), originalPoint.point());
            long usedPoint = point.use(amount);

            UserPoint updatedUserPoint = userPointTable.insertOrUpdate(id, usedPoint);
            pointHistoryTable.insert(id, amount, TransactionType.USE, System.currentTimeMillis());

            return updatedUserPoint;

        } catch (Exception e) {
            rollbackHandler.rollbackUse(id, originalPoint, originalHistorySize);
            throw e;
        }
    }

    public UserPoint getPoint(long id) {
        return userPointTable.selectById(id);
    }

    public List<PointHistory> getHistory(long id) {
        return pointHistoryTable.selectAllByUserId(id);
    }

}
