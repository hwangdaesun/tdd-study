package io.hhplus.tdd.point.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PointTest {

    @DisplayName("음수 금액으로 충전하면 false를 반환한다")
    @Test
    void charge_WithNegativeAmount_ReturnsFalse() {
        // given
        Member member = new Member(1);
        Point point = new Point(member, 1000L);
        long negativeAmount = -500L;

        // when
        boolean result = point.charge(negativeAmount);

        // then
        assertThat(result).isFalse();
    }
}
