package io.hhplus.tdd.point.domain;

import io.hhplus.tdd.point.exception.InsufficientPointException;
import io.hhplus.tdd.point.exception.InvalidChargeAmountException;
import io.hhplus.tdd.point.exception.InvalidUseAmountException;
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
        if (amount <= 0) {
            throw new InvalidChargeAmountException();
        }
        if (this.amount > MAX_POINT - amount) {
            throw new PointOverflowException();
        }
        this.amount = this.amount + amount;
        return this.amount;
    }

    public long use(long amount) {
        if (amount <= 0) {
            throw new InvalidUseAmountException();
        }
        if (this.amount < amount) {
            throw new InsufficientPointException();
        }
        this.amount = this.amount - amount;
        return this.amount;
    }
}

