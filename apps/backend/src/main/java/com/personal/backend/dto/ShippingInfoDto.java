package com.personal.backend.dto;

import com.personal.backend.domain.ShippingInfo;

import io.swagger.v3.oas.annotations.media.Schema;

public class ShippingInfoDto {
    @Schema(name = "ShippingResponse", description = "리뷰 응답")
    public record Response(
            String shippingMethod,
            Integer shippingFee,
            Integer freeShippingThreshold,
            String estimatedDeliveryDays,
            String shippingProvider
    ) {
        public static Response fromEntity(ShippingInfo shippingInfo) {
            if (shippingInfo == null) {
                return null;
            }
            return new Response(
                    shippingInfo.getShippingMethod(),
                    shippingInfo.getShippingFee(),
                    shippingInfo.getFreeShippingThreshold(),
                    shippingInfo.getEstimatedDeliveryDays(),
                    shippingInfo.getShippingProvider()
            );
        }
    }
}
