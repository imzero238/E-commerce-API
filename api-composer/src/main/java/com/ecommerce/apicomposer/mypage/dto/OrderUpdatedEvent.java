package com.ecommerce.apicomposer.mypage.dto;

import com.ecommerce.apicomposer.mypage.enums.OrderProcessingStatus;

public record OrderUpdatedEvent (

	long accountId,
	String orderEventId,
	OrderProcessingStatus orderProcessingStatus
) { }
