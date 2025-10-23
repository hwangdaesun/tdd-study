
## 동시성 제어 (Concurrency Control)

**사용자 ID 단위로 `ReentrantLock`을 사용하여 포인트 충전 및 사용 작업의 동시성을 제어**합니다. AOP(Aspect-Oriented Programming)를 활용하여 비즈니스 로직과 동시성 제어 로직을 분리했습니다.

### 핵심 구성요소

#### 1. `@PointLock` 어노테이션 (src/main/java/io/hhplus/tdd/point/aop/PointLock.java:11)

락을 적용할 메서드에 선언하는 어노테이션입니다.

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PointLock {
    long timeout() default 1;
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
```

**파라미터:**
- `timeout`: 락 획득 대기 시간 (기본값: 1)
- `timeUnit`: 시간 단위 (기본값: SECONDS)

#### 2. `PointLockAspect` (src/main/java/io/hhplus/tdd/point/aop/PointLockAspect.java:17)

AOP를 통해 `@PointLock` 어노테이션이 붙은 메서드의 동시성을 제어합니다.

```java
@Aspect
@Component
@Slf4j
public class PointLockAspect {
    private final Map<Long, ReentrantLock> userLocks = new ConcurrentHashMap<>();

    @Around("@annotation(pointLock) && args(userId, ..)")
    public Object handlePointLock(ProceedingJoinPoint joinPoint, PointLock pointLock, long userId) throws Throwable {
        ReentrantLock lock = userLocks.computeIfAbsent(userId, key -> new ReentrantLock());
        boolean lockAcquired = false;

        try {
            lockAcquired = lock.tryLock(pointLock.timeout(), pointLock.timeUnit());

            if (!lockAcquired) {
                throw new PointLockTimeoutException();
            }

            return joinPoint.proceed();

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PointProcessingInterruptedException(e);
        } finally {
            if (lockAcquired) {
                lock.unlock();
            }
        }
    }
}
```

**주요 특징:**

- **사용자별 락 관리**: `ConcurrentHashMap<Long, ReentrantLock>`을 사용하여 각 사용자 ID마다 별도의 락을 관리
- **Timeout 처리**: `tryLock(timeout, timeUnit)` 메서드로 지정된 시간 동안만 락 획득을 시도
- **Interrupt 처리**: `InterruptedException` 발생 시 스레드의 interrupt 상태를 복원하고 커스텀 예외로 변환
- **안전한 락 해제**: `finally` 블록에서 락을 획득한 경우에만 `unlock()` 호출

#### 3. `PointServiceV2` 적용 예시 (src/main/java/io/hhplus/tdd/point/service/PointServiceV2.java:26)

서비스 레이어에서 `@PointLock` 어노테이션을 사용하여 동시성 제어를 적용합니다.

```java
@Service
@RequiredArgsConstructor
@Slf4j
public class PointServiceV2 {

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
        // ... 동일한 패턴
    }
}
```

스레드가 락을 얻고 비즈니스 로직을 수행 도중에 해당 스레드가 interrupt될 수도 있기 때문에 이를 PointRollbackHandler 클래스를 통해 롤백 처리합니다.

```java
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
    
    ...
}
```

