package io.hhplus.tdd.point.controller;

import io.hhplus.tdd.point.dto.PointHistory;
import io.hhplus.tdd.point.dto.UserPoint;
import io.hhplus.tdd.point.service.PointServiceV2;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/point")
public class PointController {

    private static final Logger log = LoggerFactory.getLogger(PointController.class);
    private final PointServiceV2 pointService;

    /**
     * 특정 유저의 포인트를 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}")
    public ResponseEntity<UserPoint> point(
            @PathVariable long id
    ) {
        log.info("포인트 조회 요청 - userId: {}", id);
        UserPoint userPoint = pointService.getPoint(id);
        return ResponseEntity.ok(userPoint);
    }

    /**
     * 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}/histories")
    public ResponseEntity<List<PointHistory>> history(
            @PathVariable long id
    ) {
        log.info("포인트 내역 조회 요청 - userId: {}", id);
        List<PointHistory> histories = pointService.getHistory(id);
        return ResponseEntity.ok(histories);
    }

    /**
     * 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/charge")
    public ResponseEntity<UserPoint> charge(
            @PathVariable long id,
            @RequestBody long amount
    ) {
        log.info("포인트 충전 요청 - userId: {}, amount: {}", id, amount);
        UserPoint userPoint = pointService.charge(id, amount);
        return ResponseEntity.ok(userPoint);
    }

    /**
     * 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/use")
    public ResponseEntity<UserPoint> use(
            @PathVariable long id,
            @RequestBody long amount
    ) {
        log.info("포인트 사용 요청 - userId: {}, amount: {}", id, amount);
        UserPoint userPoint = pointService.use(id, amount);
        return ResponseEntity.ok(userPoint);
    }
}
