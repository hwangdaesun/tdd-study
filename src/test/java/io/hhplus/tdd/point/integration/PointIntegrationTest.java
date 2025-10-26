package io.hhplus.tdd.point.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.hhplus.tdd.TransactionType;
import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.dto.PointHistory;
import io.hhplus.tdd.point.dto.UserPoint;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class PointIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserPointTable userPointTable;

    @Autowired
    private PointHistoryTable pointHistoryTable;

    @BeforeEach
    void setUp() {
        userPointTable.clear();
        pointHistoryTable.clear();
    }

    @DisplayName("사용자의 포인트를 정상적으로 조회한다")
    @Test
    void getPoint_success() throws Exception {
        // given
        long userId = 1L;
        long expectedPoint = 1000L;
        userPointTable.insertOrUpdate(1L, 1000L);
        pointHistoryTable.insert(1L, 1000L, TransactionType.USE, System.currentTimeMillis());

        // when & then
        mockMvc.perform(get("/point/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.point").value(expectedPoint));
    }

    @DisplayName("사용자의 포인트 내역을 정상적으로 조회한다")
    @Test
    void getHistory_success() throws Exception {
        // given
        long userId = 2L;
        userPointTable.insertOrUpdate(userId, 1000L);
        pointHistoryTable.insert(userId, 500L, TransactionType.CHARGE, System.currentTimeMillis());
        pointHistoryTable.insert(userId, 300L, TransactionType.USE, System.currentTimeMillis());

        // when & then
        mockMvc.perform(get("/point/{id}/histories", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].userId").value(userId))
                .andExpect(jsonPath("$[0].type").value("CHARGE"))
                .andExpect(jsonPath("$[1].type").value("USE"));
    }

    @DisplayName("포인트가 정상적으로 충전되고 히스토리가 기록된다")
    @Test
    void charge_success() throws Exception {
        // given
        long userId = 3L;
        long initialPoint = 1000L;
        long chargeAmount = 500L;
        long expectedPoint = 1500L;

        userPointTable.insertOrUpdate(userId, initialPoint);

        // when & then
        mockMvc.perform(patch("/point/{id}/charge", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(chargeAmount)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.point").value(expectedPoint));

        // 포인트 테이블 검증
        UserPoint userPoint = userPointTable.selectById(userId);
        assertThat(userPoint.point()).isEqualTo(expectedPoint);

        // 히스토리 테이블 검증
        List<PointHistory> histories = pointHistoryTable.selectAllByUserId(userId);
        assertThat(histories).hasSize(1);
        assertThat(histories.get(0).amount()).isEqualTo(chargeAmount);
        assertThat(histories.get(0).type()).isEqualTo(TransactionType.CHARGE);
    }

    @DisplayName("포인트가 정상적으로 사용되고 히스토리가 기록된다")
    @Test
    void use_success() throws Exception {
        // given
        long userId = 4L;
        long initialPoint = 2000L;
        long useAmount = 500L;
        long expectedPoint = 1500L;

        userPointTable.insertOrUpdate(userId, initialPoint);

        // when & then
        mockMvc.perform(patch("/point/{id}/use", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(useAmount)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.point").value(expectedPoint));

        // 포인트 테이블 검증
        UserPoint userPoint = userPointTable.selectById(userId);
        assertThat(userPoint.point()).isEqualTo(expectedPoint);

        // 히스토리 테이블 검증
        List<PointHistory> histories = pointHistoryTable.selectAllByUserId(userId);
        assertThat(histories).hasSize(1);
        assertThat(histories.get(0).amount()).isEqualTo(useAmount);
        assertThat(histories.get(0).type()).isEqualTo(TransactionType.USE);
    }
}
