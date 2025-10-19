package io.hhplus.tdd.point.domain;

import io.hhplus.tdd.point.exception.PointOverflowException;
import lombok.Getter;

@Getter
public class Point {

    private static final long MAX_POINT = Long.MAX_VALUE;

    private Member member;
    private long amount;

    public Point(Member member, long amount) {
        this.member = member;
        this.amount = amount;
    }

    public long charge(long amount) {
        if(amount <= 0){
            throw new IllegalArgumentException("충전할 포인트는 0보다 커야 합니다.");
        }
        if(this.amount > MAX_POINT - amount){
            throw new PointOverflowException("포인트 충전 시 최대 보유 한도를 초과할 수 없습니다.");
        }
        this.amount = this.amount + amount;
        return this.amount;
    }

    public long use(long amount){
        if(amount <= 0){
            throw new IllegalArgumentException("사용할 포인트는 0보다 커야합니다.");
        }
        if(this.amount < amount){
            throw new IllegalArgumentException("포인트가 부족합니다.");
        }
        this.amount = this.amount - amount;
        return this.amount;
    }
}

