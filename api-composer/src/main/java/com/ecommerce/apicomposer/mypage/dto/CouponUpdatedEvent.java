package com.ecommerce.apicomposer.mypage.dto;

import com.ecommerce.apicomposer.mypage.enums.CouponStatus;

import java.math.BigDecimal;

public record CouponUpdatedEvent(

	long accountId,
	String couponName,
	BigDecimal discountRate,
	CouponStatus couponStatus
) { }
