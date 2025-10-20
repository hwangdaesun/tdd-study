package io.hhplus.tdd.point.service;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.controller.TransactionType;
import io.hhplus.tdd.point.dto.UserPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @Mock
    private PointHistoryTable pointHistoryTable;

    @Mock
    private UserPointTable userPointTable;

    @InjectMocks
    private PointService pointService;

    @DisplayName("포인트를 충전하면, 포인트 내역 저장을 호출한다.")
    @Test
    void charge_saveHistory() {
        // given
        long userId = 1L;
        long chargeAmount = 500L;
        UserPoint dummyUserPoint = new UserPoint(1L, 1000, System.currentTimeMillis());

        given(userPointTable.selectById(anyLong()))
                .willReturn(dummyUserPoint);
        given(userPointTable.insertOrUpdate(anyLong(), anyLong()))
                .willReturn(dummyUserPoint);

        // when
        pointService.charge(userId, chargeAmount);

        // then
        verify(pointHistoryTable, times(1)).insert(anyLong(), anyLong(), eq(TransactionType.CHARGE), anyLong());
    }

    @DisplayName("포인트를 사용하면, 포인트 내역 저장을 호출한다.")
    @Test
    void use_saveHistory(){
        // given
        long userId = 1L;
        long chargeAmount = 500L;
        UserPoint dummyUserPoint = new UserPoint(1L, 1000, System.currentTimeMillis());

        given(userPointTable.selectById(anyLong()))
                .willReturn(dummyUserPoint);
        given(userPointTable.insertOrUpdate(anyLong(), anyLong()))
                .willReturn(dummyUserPoint);

        // when
        pointService.use(userId, chargeAmount);

        // then
        verify(pointHistoryTable, times(1)).insert(anyLong(), anyLong(), eq(TransactionType.USE), anyLong());
    }
}
