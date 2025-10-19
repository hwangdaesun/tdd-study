package io.hhplus.tdd.point;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.domain.Member;
import io.hhplus.tdd.point.domain.Point;
import io.hhplus.tdd.point.dto.UserPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointHistoryTable pointHistoryTable;
    private final UserPointTable userPointTable;

    public UserPoint charge(long id, long amount){
        UserPoint userPoint = userPointTable.selectById(id);
        Point point = new Point(new Member(userPoint.id()), userPoint.point());
        long chargedPoint = point.charge(amount);
        UserPoint updatedUserPoint = userPointTable.insertOrUpdate(id, chargedPoint);
        pointHistoryTable.insert(id, amount, TransactionType.CHARGE, System.currentTimeMillis());
        return updatedUserPoint;
    }

    public UserPoint use(long id, long amount){
        UserPoint userPoint = userPointTable.selectById(id);
        Point point = new Point(new Member(userPoint.id()), userPoint.point());
        long usedPoint = point.use(amount);
        UserPoint updatedUserPoint = userPointTable.insertOrUpdate(id, usedPoint);
        pointHistoryTable.insert(id, amount, TransactionType.USE, System.currentTimeMillis());
        return updatedUserPoint;
    }

}
