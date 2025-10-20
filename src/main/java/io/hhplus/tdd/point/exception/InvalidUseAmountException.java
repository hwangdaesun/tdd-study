package io.hhplus.tdd.point.exception;

public class InvalidUseAmountException extends CustomException {
    public InvalidUseAmountException() {
        super(ErrorCode.INVALID_USE_AMOUNT);
    }
}
