package com.ecommerce.orderservice.internalevent;

import com.ecommerce.orderservice.order.dto.OrderDto;
import com.ecommerce.orderservice.order.service.OrderInquiryService;
import com.ecommerce.orderservice.internalevent.ordercreation.OrderCreationInternalEvent;
import com.ecommerce.orderservice.internalevent.service.InternalEventService;
import com.ecommerce.orderservice.kafka.config.TopicConfig;
import com.ecommerce.orderservice.kafka.dto.OrderKafkaEvent;
import com.ecommerce.orderservice.kafka.service.producer.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@RequiredArgsConstructor
@Component
@Slf4j
public class InternalEventListener {

    private final InternalEventService internalEventService;
    private final KafkaProducerService kafkaProducerService;
    private final OrderInquiryService orderInquiryService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void insertOrderCreationInternalEvent(OrderCreationInternalEvent event) {
        internalEventService.saveOrderCreationInternalEvent(event);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void createKafkaEvent(OrderCreationInternalEvent event) {
        OrderDto orderDto = orderInquiryService.findOrderByOrderEventId(event.getOrderEventId());
        kafkaProducerService.send(TopicConfig.REQUESTED_ORDER_TOPIC, orderDto.getOrderEventId(), OrderKafkaEvent.of(orderDto));
    }
}
