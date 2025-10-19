package io.hhplus.tdd.point.domain;

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
                .isInstanceOf(IllegalArgumentException.class);
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
                .isInstanceOf(IllegalArgumentException.class);
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
                .isInstanceOf(IllegalArgumentException.class);
        Assertions.assertEquals(currentAmount, point.getAmount());
    }

    @DisplayName("보유 포인트와 정확히 같은 금액을 사용하면 포인트가 0이 된다")
    @Test
    void use_ExactAmount_Success(){
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

}
