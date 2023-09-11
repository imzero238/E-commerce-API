package com.nayoung.orderservice.messagequeue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nayoung.orderservice.domain.OrderService;
import com.nayoung.orderservice.messagequeue.client.ItemServiceClient;
import com.nayoung.orderservice.web.dto.ItemUpdateLogDto;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KafkaConsumer {

    private final OrderService orderService;
    private final ItemServiceClient itemServiceClient;

    @KafkaListener(topics = "e-commerce.order.order-details")
    public void updateOrderStatus(ConsumerRecord<String, String> record) {
        try {
            waitBasedOnTimestamp(record.timestamp());
            ObjectMapper mapper = new ObjectMapper();
            Map<Object, Object> map = mapper.readValue(record.value(), new TypeReference<Map<Object, Object>>() {});
            Long orderId = Long.parseLong(String.valueOf(map.get("orderId")));
            List<ItemUpdateLogDto> itemUpdateLogDtoList = itemServiceClient.getItemUpdateLogDtos(orderId);
            if(!itemUpdateLogDtoList.isEmpty())
                orderService.updateOrderStatus(itemUpdateLogDtoList);
        } catch (InterruptedException | JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    private void waitBasedOnTimestamp(long recordTimestamp) throws InterruptedException {
        Instant recordAppendTime = Instant.ofEpochMilli(recordTimestamp);
        Instant now = Instant.now();
        while(now.toEpochMilli() - recordAppendTime.toEpochMilli() < 5000) {
            Thread.sleep(1000);
            now = Instant.now();
        }
    }
}