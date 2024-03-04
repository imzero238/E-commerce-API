package com.ecommerce.itemservice.domain.item.api;

import com.ecommerce.itemservice.domain.item.dto.ItemRegisterRequest;
import com.ecommerce.itemservice.exception.OrderException;
import com.ecommerce.itemservice.kafka.dto.OrderItemStatus;
import com.ecommerce.itemservice.domain.item.dto.ItemDto;
import com.ecommerce.itemservice.domain.item.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/")
public class ItemController {

    private final ItemService itemService;

    @PostMapping("/items/create")
    public ResponseEntity<?> create(@RequestBody @Valid ItemRegisterRequest request) {
        ItemDto response = itemService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/order-processing-result/{eventId}")
    public ResponseEntity<?> findAllItemUpdateLogByEventId(@PathVariable String eventId) {
        try {
            OrderItemStatus orderItemStatus = itemService.findOrderProcessingStatus(eventId);
            return ResponseEntity.status(HttpStatus.OK).body(orderItemStatus);
        } catch (OrderException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e);
        }
    }
}