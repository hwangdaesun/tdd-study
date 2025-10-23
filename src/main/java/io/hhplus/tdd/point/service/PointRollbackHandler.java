package io.hhplus.tdd.point.service;

import io.hhplus.tdd.TransactionType;
import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.dto.PointHistory;
import io.hhplus.tdd.point.dto.UserPoint;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PointRollbackHandler {

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    public void rollbackCharge(long userId, UserPoint originalPoint, int originalHistorySize) {
        try {
            userPointTable.insertOrUpdate(userId, originalPoint.point());

            List<PointHistory> currentHistories = pointHistoryTable.selectAllByUserId(userId);
            int currentHistorySize = currentHistories.size();

            if (currentHistorySize > originalHistorySize) {
                pointHistoryTable.insert(userId, currentHistories.get(currentHistorySize - 1).amount(),
                        TransactionType.USE, System.currentTimeMillis());
            }

        } catch (Exception rollbackException) {
            log.error("롤백 실패! userId={}", userId, rollbackException);
        }
    }

    public void rollbackUse(long userId, UserPoint originalPoint, int originalHistorySize) {
        try {
            userPointTable.insertOrUpdate(userId, originalPoint.point());

            List<PointHistory> currentHistories = pointHistoryTable.selectAllByUserId(userId);
            int currentHistorySize = currentHistories.size();

            if (currentHistorySize > originalHistorySize) {
                pointHistoryTable.insert(userId, currentHistories.get(currentHistorySize - 1).amount(),
                        TransactionType.CHARGE, System.currentTimeMillis());
            }

        } catch (Exception rollbackException) {
            log.error("롤백 실패 userId={}", userId, rollbackException);
        }
    }
}