package io.hhplus.tdd.point.exception;

public class InsufficientPointException extends CustomException {
    public InsufficientPointException() {
        super(ErrorCode.INSUFFICIENT_POINT);
    }
}