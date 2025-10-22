package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.dto.UserPoint;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

class PointServiceConcurrencyTest {

    private UserPointTable userPointTable;
    private PointHistoryTable pointHistoryTable;
    private PointService pointService;

    @BeforeEach
    void setUp() {
        userPointTable = new UserPointTable();
        pointHistoryTable = new PointHistoryTable();
        pointService = new PointService(pointHistoryTable, userPointTable);
    }

    @DisplayName("동일한 사용자가 동시에 포인트를 충전하면, 모든 충전이 정확히 반영되어야 한다")
    @Test
    void concurrentCharge_shouldReflectAllCharges() throws InterruptedException {
        // given
        long userId = 1L;
        long chargeAmount = 100L;
        int threadCount = 10;

        // 초기 포인트 설정
        userPointTable.insertOrUpdate(userId, 0L);

        // when
        executeConcurrently(threadCount, () -> {
            pointService.charge(userId, chargeAmount);
        });

        // then
        UserPoint result = userPointTable.selectById(userId);
        assertThat(result.point()).isEqualTo(threadCount * chargeAmount);
    }

    @DisplayName("동일한 사용자가 대해 동시에 포인트를 사용하면, 모든 사용이 정확히 반영되어야 한다")
    @Test
    void concurrentUse_shouldReflectAllUses() throws InterruptedException {
        // given
        long userId = 1L;
        long useAmount = 100L;
        int threadCount = 10;
        long initialPoint = threadCount * useAmount;

        // 초기 포인트 설정
        userPointTable.insertOrUpdate(userId, initialPoint);

        // when
        executeConcurrently(threadCount, () -> {
            pointService.use(userId, useAmount);
        });

        // then
        UserPoint result = userPointTable.selectById(userId);
        assertThat(result.point()).isEqualTo(0L);
    }

    /**
     * 여러 스레드를 동시에 시작하여 작업을 실행하는 헬퍼 메서드
     *
     * @param threadCount 실행할 스레드 수
     * @param task 각 스레드에서 실행할 작업
     */
    private void executeConcurrently(int threadCount, Runnable task) throws InterruptedException {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadCount);
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CyclicBarrier startBarrier = new CyclicBarrier(threadCount);
        CountDownLatch completeLatch = new CountDownLatch(threadCount);

        try {
            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        readyLatch.countDown(); // 준비 완료 신호
                        startBarrier.await();   // 모든 스레드가 준비될 때까지 대기
                        task.run();             // 실제 작업 실행
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        completeLatch.countDown(); // 완료 신호
                    }
                });
            }

            // 모든 스레드가 준비될 때까지 대기
            readyLatch.await();
            // 모든 스레드가 작업을 완료할 때까지 대기 (최대 10초)
            boolean completed = completeLatch.await(10, TimeUnit.SECONDS);
            assertThat(completed).isTrue();
        } finally {
            executor.shutdown();
        }
    }
}
