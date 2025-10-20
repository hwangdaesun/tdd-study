package io.hhplus.tdd.point.dto;

import io.hhplus.tdd.point.controller.TransactionType;

public record PointHistory(
        long id,
        long userId,
        long amount,
        TransactionType type,
        long updateMillis
) {
}
