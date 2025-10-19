package io.hhplus.tdd.point.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
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


}
