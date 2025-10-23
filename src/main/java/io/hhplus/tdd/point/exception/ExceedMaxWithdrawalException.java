package io.hhplus.tdd.point.exception;

public class ExceedMaxWithdrawalException extends CustomException {
    public ExceedMaxWithdrawalException() {
        super(ErrorCode.EXCEED_MAX_WITHDRAWAL);
    }
}