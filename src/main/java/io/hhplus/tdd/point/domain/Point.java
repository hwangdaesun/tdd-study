package io.hhplus.tdd.point.domain;

import lombok.Getter;

@Getter
public class Point {

    private Member member;
    private long amount;

    public Point(Member member, long amount) {
        this.member = member;
        this.amount = amount;
    }

    public void charge(long amount) {
        if( amount <= 0){
            throw new IllegalArgumentException("충전할 포인트는 0보다 커야 합니다.");
        }
        this.amount += amount;
    }
}

