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

    public boolean charge(long amount) {
        if( amount <= 0){
            return false;
        }
        this.amount += amount;
        return true;
    }
}

