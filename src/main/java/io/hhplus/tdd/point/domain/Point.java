package io.hhplus.tdd.point.domain;

import io.hhplus.tdd.point.exception.ExceedMaxWithdrawalException;
import io.hhplus.tdd.point.exception.InsufficientPointException;
import io.hhplus.tdd.point.exception.InvalidChargeAmountException;
import io.hhplus.tdd.point.exception.InvalidUseAmountException;
import io.hhplus.tdd.point.exception.InvalidUseUnitException;
import io.hhplus.tdd.point.exception.PointOverflowException;
import lombok.Getter;

@Getter
public class Point {

    private static final long MAX_POINT = Long.MAX_VALUE;
    private static final long MAX_WITHDRAWAL_AMOUNT = 1_000_000L;

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
        if (amount > MAX_WITHDRAWAL_AMOUNT) {
            throw new ExceedMaxWithdrawalException();
        }
        if (amount % 100 != 0) {
            throw new InvalidUseUnitException();
        }
        if (this.amount < amount) {
            throw new InsufficientPointException();
        }
        this.amount = this.amount - amount;
        return this.amount;
    }
}

