package com.ecommerce.couponservice.domain.coupon.service;

import com.ecommerce.couponservice.domain.coupon.dto.WaitQueuePositionResponseDto;
import com.ecommerce.couponservice.redis.manager.CouponQueueRedisManager;
import com.ecommerce.couponservice.redis.manager.CouponStockRedisManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class CouponQueueServiceUnitTest {

    @InjectMocks
    private CouponQueueService couponQueueService;

    @Mock
    private CouponQueueRedisManager couponQueueRedisManager;

    @Mock
    private CouponStockRedisManager couponStockRedisManager;

    @DisplayName("쿠폰 발급 요청 시 대기열에 진입해야 한다.")
    @Test
    void shouldEnterWaitQueueWhenRequestingCouponIssuance () {
        // setup(data)
        final Long couponId = 1L;
        final Long accountId = 1L;

        // setup(expectations)
        when(couponStockRedisManager.getStock(couponId)).thenReturn(Optional.of(2L));
        doNothing().when(couponQueueRedisManager).addCouponWaitQueue(anyLong(), anyLong());
        when(couponQueueRedisManager.getWaitQueueRank(anyLong(), anyLong())).thenReturn(1L);

        // exercise
        couponQueueService.addToCouponWaitQueue(couponId, accountId);

        // verify
        verify(couponQueueRedisManager, times(1))
                .addCouponWaitQueue(anyLong(), anyLong());

        verify(couponQueueRedisManager, times(1))
                .getWaitQueueRank(anyLong(), anyLong());
    }

    @DisplayName("존재하는 쿠폰이면 대기열에 진입하고 정상 응답해야 한다.")
    @Test
    void shouldReturnCorrectResponseWhenEnteringExistingCouponWaitQueue () {
        // setup(data)
        final Long couponId = 1L;
        final Long accountId = 1L;
        final long rank = 1L;
        final long expectedPosition = rank + 1;

        // setup(expectations)
        when(couponStockRedisManager.getStock(couponId)).thenReturn(Optional.of(2L));
        doNothing().when(couponQueueRedisManager).addCouponWaitQueue(anyLong(), anyLong());
        when(couponQueueRedisManager.getWaitQueueRank(anyLong(), anyLong())).thenReturn(rank);

        // exercise
        WaitQueuePositionResponseDto response = couponQueueService.addToCouponWaitQueue(couponId, accountId);

        // verify
        assertThat(response).isNotNull();
        assertTrue(response.isExists());
        assertEquals(expectedPosition, response.getPosition());

        String expectedMessage = String.format("Account %d is in position %d for coupon %d", accountId, expectedPosition, couponId);
        assertEquals(expectedMessage, response.getMessage());
    }

    @DisplayName("존재하지 않는 쿠폰에 대해 대기열 진입 시 적절한 응답을 반환해야 한다.")
    @Test
    void shouldReturnAppropriateResponseForNonExistentCoupon() {
        // setup(data)
        final Long couponId = 1L;
        final Long accountId = 1L;

        // setup(expectations)
        when(couponStockRedisManager.getStock(couponId)).thenReturn(Optional.empty());

        // exercise
        WaitQueuePositionResponseDto response = couponQueueService.addToCouponWaitQueue(couponId, accountId);

        // verify
        assertThat(response).isNotNull();
        assertFalse(response.isExists());
        assertNull(response.getPosition());

        String expectedMessage = String.format("The wait queue for coupon %d does not exist", couponId);
        assertEquals(expectedMessage, response.getMessage());
    }

    @DisplayName("존재하는 쿠폰이지만, 대기열에 없는 사용자에 대해 적절한 응답을 반환해야 한다.")
    @Test
    void shouldReturnAppropriateResponseForExistingCouponButAccountNotInQueue() {
        // setup(data)
        final Long couponId = 1L;
        final Long accountId = 1L;
        final Long rank = null;

        // setup(expectations)
        when(couponStockRedisManager.getStock(couponId)).thenReturn(Optional.of(2L));
        doNothing().when(couponQueueRedisManager).addCouponWaitQueue(anyLong(), anyLong());
        when(couponQueueRedisManager.getWaitQueueRank(anyLong(), anyLong())).thenReturn(rank);

        // exercise
        WaitQueuePositionResponseDto response = couponQueueService.addToCouponWaitQueue(couponId, accountId);

        // verify
        assertThat(response).isNotNull();
        assertFalse(response.isExists());
        assertNull(response.getPosition());

        String expectedMessage = String.format("The wait queue for coupon %d does not contain account %d", couponId, accountId);
        assertEquals(expectedMessage, response.getMessage());
    }
}