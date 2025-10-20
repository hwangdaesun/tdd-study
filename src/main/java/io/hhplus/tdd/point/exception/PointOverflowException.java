package io.hhplus.tdd.point.exception;

public class PointOverflowException extends CustomException {
    public PointOverflowException() {
        super(ErrorCode.POINT_OVERFLOW);
    }
}