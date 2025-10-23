package io.hhplus.tdd.point.exception;

public class InvalidUseUnitException extends CustomException {
    public InvalidUseUnitException() {
        super(ErrorCode.INVALID_USE_UNIT);
    }
}