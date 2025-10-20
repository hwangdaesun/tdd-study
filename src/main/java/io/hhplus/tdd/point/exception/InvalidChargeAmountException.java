package io.hhplus.tdd.point.exception;

public class InvalidChargeAmountException extends CustomException {
    public InvalidChargeAmountException() {
        super(ErrorCode.INVALID_CHARGE_AMOUNT);
    }
}
