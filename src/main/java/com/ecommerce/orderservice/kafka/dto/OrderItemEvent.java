package com.ecommerce.orderservice.kafka.dto;

import com.ecommerce.orderservice.domain.order.OrderItem;
import com.ecommerce.orderservice.domain.order.OrderStatus;
import com.ecommerce.orderservice.domain.order.dto.OrderItemDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemEvent {

    private Long itemId;
    private Long quantity;
    private OrderStatus orderStatus;

    public static OrderItemEvent of(OrderItem orderItem) {
        return OrderItemEvent.builder()
                .itemId(orderItem.getItemId())
                .quantity(orderItem.getQuantity())
                .orderStatus(orderItem.getOrderStatus() != null ? orderItem.getOrderStatus() : null)
                .build();
    }

    public static OrderItemEvent of(OrderItemDto orderItemDto) {
        return OrderItemEvent.builder()
                .itemId(orderItemDto.getItemId())
                .quantity(orderItemDto.getQuantity())
                .orderStatus(orderItemDto.getOrderStatus() != null ? orderItemDto.getOrderStatus() : null)
                .build();
    }

    public void updateOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}
