package io.hhplus.tdd.point.controller;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import io.hhplus.tdd.point.PointController;
import io.hhplus.tdd.point.exception.ErrorCode;
import io.hhplus.tdd.point.exception.InvalidChargeAmountException;
import io.hhplus.tdd.point.service.PointService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PointController.class)
class PointControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PointService pointService;

    @DisplayName("포인트 충전 시 InvalidChargeAmountException 발생하면 적절하게 예외 처리가 된다.")
    @Test
    void charge_throwsInvalidChargeAmountException_correctly_handled_by_advice() throws Exception {
        // given
        long userId = 1L;
        long invalidAmount = -100L;

        given(pointService.charge(anyLong(), anyLong()))
                .willThrow(new InvalidChargeAmountException());

        // when & then
        mockMvc.perform(patch("/point/{id}/charge", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.valueOf(invalidAmount)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_CHARGE_AMOUNT.getCode()))
                .andExpect(jsonPath("$.message").value(ErrorCode.INVALID_CHARGE_AMOUNT.getMessage()));
    }
}
