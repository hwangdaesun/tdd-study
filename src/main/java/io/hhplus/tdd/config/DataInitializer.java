package io.hhplus.tdd.config;

import io.hhplus.tdd.TransactionType;
import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 애플리케이션 시작 시 초기 데이터를 설정하는 컴포넌트
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    private static final long INITIAL_USER_ID = 1L;
    private static final long INITIAL_POINT_AMOUNT = 100L;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Initializing data for user {}", INITIAL_USER_ID);

        // 유저 1L에 대한 초기 포인트 설정 (0 포인트)
        userPointTable.insertOrUpdate(INITIAL_USER_ID, INITIAL_POINT_AMOUNT);

        // 초기 히스토리 기록
        pointHistoryTable.insert(
            INITIAL_USER_ID,
            INITIAL_POINT_AMOUNT,
            TransactionType.CHARGE,
            System.currentTimeMillis()
        );
    }
}
