package com.ecommerce.couponservice.redis.scheduler;

import com.ecommerce.couponservice.redis.manager.CouponQueueRedisManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class MoveFromWaitToEnterQueueScheduler extends BaseCouponScheduler {

    private final CouponQueueRedisManager couponQueueRedisManager;

    @Scheduled(fixedDelay = 5_000, initialDelay = 10_000)
    public void processScheduledWaitQueueTasks() {
        Set<String> waitKeys = couponQueueRedisManager.getWaitQueueKeys();
        if(waitKeys.isEmpty()) {
            log.debug("No wait queues found to process.");
            return;
        }

        waitKeys.forEach(this::processWaitQueue);
    }

    private void processWaitQueue(String waitKey) {
        try {
            Long couponId = extractCouponId(CouponQueueRedisManager.WAIT_KEY_PREFIX, waitKey);
            if(couponId == null) {
                log.warn("Invalid wait queue key format: {}", waitKey);
                return;
            }

            long movedCount = couponQueueRedisManager.moveFromWaitToEnterQueue(couponId);
            log.info("Moved {} users from wait queue to enter queue for couponId: {}", movedCount, couponId);
        } catch (NumberFormatException e) {
            log.error("Failed to parse wait queue key: {}", waitKey, e);
        } catch (Exception e) {
            log.error("Failed to process wait queue key: {}", waitKey, e);
        }
    }
}
