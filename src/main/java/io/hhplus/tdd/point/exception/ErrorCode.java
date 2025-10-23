package io.hhplus.tdd.point.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Point Charge Errors
    INVALID_CHARGE_AMOUNT("POINT_001", "충전할 포인트는 0보다 커야 합니다.", HttpStatus.BAD_REQUEST),
    POINT_OVERFLOW("POINT_002", "포인트 충전 시 최대 보유 한도를 초과할 수 없습니다.", HttpStatus.BAD_REQUEST),

    // Point Use Errors
    INVALID_USE_AMOUNT("POINT_003", "사용할 포인트는 0보다 커야합니다.", HttpStatus.BAD_REQUEST),
    INSUFFICIENT_POINT("POINT_004", "포인트가 부족합니다.", HttpStatus.BAD_REQUEST),
    INVALID_USE_UNIT("POINT_007", "포인트는 100원 단위로만 사용할 수 있습니다.", HttpStatus.BAD_REQUEST),
    EXCEED_MAX_WITHDRAWAL("POINT_008", "한 번에 100만원 이상 출금할 수 없습니다.", HttpStatus.BAD_REQUEST),

    // Concurrency Errors
    POINT_LOCK_TIMEOUT("POINT_005", "포인트 처리가 지연되고 있습니다. 잠시 후 다시 시도해주세요.", HttpStatus.CONFLICT),
    POINT_PROCESSING_INTERRUPTED("POINT_006", "포인트 처리가 중단되었습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}