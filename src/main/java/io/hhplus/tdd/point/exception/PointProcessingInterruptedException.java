package io.hhplus.tdd.point.exception;

public class PointProcessingInterruptedException extends CustomException {
    public PointProcessingInterruptedException(Throwable cause) {
        super(ErrorCode.POINT_PROCESSING_INTERRUPTED);
        initCause(cause);
    }
}