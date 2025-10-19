package io.hhplus.tdd.point.domain;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class PointTest {

    @DisplayName("음수 금액 또는 0원으로 충전하면 false를 반환한다")
    @ParameterizedTest
    @ValueSource(longs = {-1000L, -500L, -100L, -1L, 0L})
    void charge_WithNegativeAmount_ReturnsFalse(long invalidAmount) {
        // given
        long currentAmount = 1000L;
        Member member = new Member(1);
        Point point = new Point(member, currentAmount);

        // when
        boolean result = point.charge(invalidAmount);

        // then
        assertThat(result).isFalse();
        Assertions.assertEquals(currentAmount, point.getAmount());
    }


}
