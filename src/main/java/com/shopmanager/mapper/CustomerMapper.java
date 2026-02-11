package com.shopmanager.mapper;

import com.shopmanager.dto.customer.CustomerRequest;
import com.shopmanager.dto.customer.CustomerResponse;
import com.shopmanager.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    Customer toEntity(CustomerRequest dto);

    @Mapping(target = "createdAt", expression = "java(entity.getCreatedAt().toString())")
    CustomerResponse toResponse(Customer entity);
}