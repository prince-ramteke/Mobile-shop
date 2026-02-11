package com.shopmanager.mapper;

import com.shopmanager.dto.payment.PaymentResponse;
import com.shopmanager.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "saleId", source = "sale.id")
    @Mapping(target = "paidAt", expression = "java(payment.getPaidAt().toString())")
    PaymentResponse toResponse(Payment payment);
}