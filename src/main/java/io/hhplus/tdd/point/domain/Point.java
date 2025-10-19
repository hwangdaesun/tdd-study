package io.hhplus.tdd.point.domain;

import java.math.BigInteger;
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

    public BigInteger use(BigInteger amount){
        if(amount.compareTo(BigInteger.ZERO) <= 0){
            throw new IllegalArgumentException("사용할 포인트는 0보다 커야합니다.");
        }
        if(this.amount.compareTo(amount) < 0){
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }
        this.amount = this.amount.subtract(amount);
        return this.amount;
    }
}

