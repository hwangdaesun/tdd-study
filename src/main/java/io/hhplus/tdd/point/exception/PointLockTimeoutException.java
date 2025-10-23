package io.hhplus.tdd.point.exception;

public class PointLockTimeoutException extends CustomException {
    public PointLockTimeoutException() {
        super(ErrorCode.POINT_LOCK_TIMEOUT);
    }
}