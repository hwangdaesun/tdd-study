package io.hhplus.tdd.point.domain;

import java.math.BigInteger;
import java.sql.SQLOutput;
import lombok.Getter;

@Getter
public class Point {

    private Member member;
    private BigInteger amount;

    public Point(Member member, BigInteger amount) {
        this.member = member;
        this.amount = amount;
    }

    public BigInteger charge(BigInteger amount) {
        if(amount.compareTo(BigInteger.ZERO) <= 0){
            throw new IllegalArgumentException("충전할 포인트는 0보다 커야 합니다.");
        }
        this.amount = this.amount.add(amount);
        return this.amount;
    }
}

