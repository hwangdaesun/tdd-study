package io.hhplus.tdd.point.integration;

import static org.assertj.core.api.Assertions.assertThat;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.dto.UserPoint;
import io.hhplus.tdd.point.service.PointServiceV2;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class PointServiceV2ConcurrencyTest {

    @Autowired
    private UserPointTable userPointTable;

    @Autowired
    private PointHistoryTable pointHistoryTable;

    @Autowired
    private PointServiceV2 pointServiceV2;

    @DisplayName("동일한 사용자가 동시에 포인트를 충전하면, 성공한 충전만 정확히 반영되어야 한다")
    @Test
    void concurrentCharge_shouldReflectAllCharges() throws InterruptedException {
        // given
        long userId = 1L;
        long chargeAmount = 100L;
        int threadCount = 10;
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // 초기 포인트 설정
        userPointTable.insertOrUpdate(userId, 0L);

        // when
        executeConcurrently(threadCount, () -> {
            try {
                pointServiceV2.charge(userId, chargeAmount);
                successCount.incrementAndGet();
            } catch (RuntimeException e) {
                failCount.incrementAndGet();
            }
        });

        // then
        UserPoint result = userPointTable.selectById(userId);
        int totalAttempts = successCount.get() + failCount.get();

        assertThat(totalAttempts).isEqualTo(threadCount); // 모든 시도가 처리됨
        assertThat(result.point()).isEqualTo(successCount.get() * chargeAmount); // 성공한 만큼만 반영

        System.out.println("충전 테스트 - 성공: " + successCount.get() + ", 실패: " + failCount.get());
    }

    @DisplayName("동일한 사용자가 대해 동시에 포인트를 사용하면, 성공한 사용만 정확히 반영되어야 한다")
    @Test
    void concurrentUse_shouldReflectAllUses() throws InterruptedException {
        // given
        long userId = 1L;
        long useAmount = 100L;
        int threadCount = 10;
        long initialPoint = threadCount * useAmount;
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        // 초기 포인트 설정
        userPointTable.insertOrUpdate(userId, initialPoint);

        // when
        executeConcurrently(threadCount, () -> {
            try {
                pointServiceV2.use(userId, useAmount);
                successCount.incrementAndGet();
            } catch (RuntimeException e) {
                failCount.incrementAndGet();
            }
        });

        // then
        UserPoint result = userPointTable.selectById(userId);
        int totalAttempts = successCount.get() + failCount.get();

        assertThat(totalAttempts).isEqualTo(threadCount); // 모든 시도가 처리됨
        assertThat(result.point()).isEqualTo(initialPoint - (successCount.get() * useAmount)); // 성공한 만큼만 차감

        System.out.println("사용 테스트 - 성공: " + successCount.get() + ", 실패: " + failCount.get());
    }

    /**
     * 여러 스레드를 동시에 시작하여 작업을 실행하는 헬퍼 메서드
     *
     * @param threadCount 실행할 스레드 수
     * @param task 각 스레드에서 실행할 작업
     */
    private void executeConcurrently(int threadCount, Runnable task) throws InterruptedException {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadCount);
        CyclicBarrier startBarrier = new CyclicBarrier(threadCount);
        CountDownLatch completeLatch = new CountDownLatch(threadCount);

        try {
            for (int i = 0; i < threadCount; i++) {
                executor.submit(() -> {
                    try {
                        startBarrier.await();   // 모든 스레드가 준비될 때까지 대기
                        task.run();             // 실제 작업 실행
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        completeLatch.countDown(); // 완료 신호
                    }
                });
            }

            // 모든 스레드가 작업을 완료할 때까지 대기 (최대 10초)
            boolean completed = completeLatch.await(10, TimeUnit.SECONDS);
            assertThat(completed).isTrue();
        } finally {
            executor.shutdown();
        }
    }
}
