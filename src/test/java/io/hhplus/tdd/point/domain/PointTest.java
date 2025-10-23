package io.hhplus.tdd.point.domain;

import io.hhplus.tdd.point.exception.InsufficientPointException;
import io.hhplus.tdd.point.exception.InvalidChargeAmountException;
import io.hhplus.tdd.point.exception.InvalidUseAmountException;
import io.hhplus.tdd.point.exception.InvalidUseUnitException;
import io.hhplus.tdd.point.exception.PointOverflowException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class PointTest {

    @DisplayName("음수 금액 또는 0원으로 충전하면 예외를 발생시킨다")
    @ParameterizedTest
    @ValueSource(longs = {-1000L, -500L, -100L, -1L, 0L})
    void charge_WithNegativeAmount_ThrowsException(long invalidAmount) {
        // given
        long currentAmount = 1000L;
        Member member = new Member(1);
        Point point = new Point(member, currentAmount);

        // when && then
        assertThatThrownBy(() -> point.charge(invalidAmount))
                .isInstanceOf(InvalidChargeAmountException.class);
        Assertions.assertEquals(currentAmount, point.getAmount());
    }

    @DisplayName("충전 후 Long.MAX_VALUE를 초과하면 예외를 발생시킨다")
    @Test
    void charge_ExceedsMaxValue_ThrowsException() {
        // given
        Member member = new Member(1);
        long currentAmount = Long.MAX_VALUE - 100;
        Point point = new Point(member, currentAmount);
        long chargeAmount = 200L;

        // when && then
        assertThatThrownBy(() -> point.charge(chargeAmount))
                .isInstanceOf(PointOverflowException.class);
    }

    @DisplayName("충전 후 정확히 Long.MAX_VALUE가 되면 정상적으로 충전된다")
    @Test
    void charge_EqualsMaxValue_Success() {
        // given
        Member member = new Member(1);
        long currentAmount = Long.MAX_VALUE - 1000;
        Point point = new Point(member, currentAmount);
        long chargeAmount = 1000L;

        // when
        long result = point.charge(chargeAmount);

        // then
        Assertions.assertEquals(Long.MAX_VALUE, result);
    }

    @DisplayName("포인트가 정상적으로 충전된다")
    @Test
    void charge_Success() {
        // given
        Member member = new Member(1);
        long currentAmount = 1000L;
        Point point = new Point(member, currentAmount);
        long chargeAmount = 500L;
        long expectedAmount = 1500L;

        // when
        long result = point.charge(chargeAmount);

        // then
        Assertions.assertEquals(expectedAmount, result);
        Assertions.assertEquals(expectedAmount, point.getAmount());
    }

    @DisplayName("음수 금액 또는 0원으로 포인트를 사용하면 예외를 발생시킨다")
    @ParameterizedTest
    @ValueSource(longs = {-1000L, -500L, -100L, -1L, 0L})
    void use_WithNegativeOrZeroAmount_ThrowsException(long invalidAmount) {
        // given
        long currentAmount = 10000L;
        Member member = new Member(1);
        Point point = new Point(member, currentAmount);

        // when && then
        assertThatThrownBy(() -> point.use(invalidAmount))
                .isInstanceOf(InvalidUseAmountException.class);
        Assertions.assertEquals(currentAmount, point.getAmount());
    }

    @DisplayName("보유 포인트보다 많은 금액을 사용하려고 하면 예외를 발생시킨다")
    @Test
    void use_BigAmount_ThrowsException() {
        // given
        long currentAmount = 1000L;
        Member member = new Member(1);
        Point point = new Point(member, currentAmount);
        long useAmount = 1500L;

        // when && then
        assertThatThrownBy(() -> point.use(useAmount))
                .isInstanceOf(InsufficientPointException.class);
        Assertions.assertEquals(currentAmount, point.getAmount());
    }

    @DisplayName("보유 포인트와 정확히 같은 금액을 사용하면 포인트가 0이 된다")
    @Test
    void use_ExactAmount_Success() {
        // given
        long currentAmount = 1000L;
        Member member = new Member(1);
        Point point = new Point(member, currentAmount);
        long useAmount = 1000L;

        // when
        long result = point.use(useAmount);

        // then
        Assertions.assertEquals(0L, result);
    }

    @DisplayName("포인트가 정상적으로 사용된다")
    @Test
    void use_Success() {
        // given
        Member member = new Member(1);
        long currentAmount = 2000L;
        Point point = new Point(member, currentAmount);
        long useAmount = 500L;
        long expectedAmount = 1500L;

        // when
        long result = point.use(useAmount);

        // then
        Assertions.assertEquals(expectedAmount, result);
        Assertions.assertEquals(expectedAmount, point.getAmount());
    }

    @DisplayName("100원 단위가 아닌 포인트를 사용하려고 하면 예외를 발생시킨다")
    @ParameterizedTest
    @ValueSource(longs = {1L, 10L, 50L, 99L, 101L, 150L, 199L, 250L, 999L, 1001L})
    void use_WithNonHundredUnit_ThrowsException(long invalidAmount) {
        // given
        long currentAmount = 10000L;
        Member member = new Member(1);
        Point point = new Point(member, currentAmount);

        // when && then
        assertThatThrownBy(() -> point.use(invalidAmount))
                .isInstanceOf(InvalidUseUnitException.class);
        Assertions.assertEquals(currentAmount, point.getAmount());
    }

    @DisplayName("100원 단위의 포인트는 정상적으로 사용된다")
    @ParameterizedTest
    @ValueSource(longs = {100L, 200L, 300L, 500L, 1000L, 1500L, 2000L})
    void use_WithHundredUnit_Success(long validAmount) {
        // given
        long currentAmount = 10000L;
        Member member = new Member(1);
        Point point = new Point(member, currentAmount);
        long expectedAmount = currentAmount - validAmount;

        // when
        long result = point.use(validAmount);

        // then
        Assertions.assertEquals(expectedAmount, result);
        Assertions.assertEquals(expectedAmount, point.getAmount());
    }

}
