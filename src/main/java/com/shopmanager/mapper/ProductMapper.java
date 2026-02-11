package com.shopmanager.mapper;

import com.shopmanager.dto.product.ProductRequest;
import com.shopmanager.dto.product.ProductResponse;
import com.shopmanager.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    Product toEntity(ProductRequest dto);

    @Mapping(target = "createdAt", expression = "java(entity.getCreatedAt().toString())")
    ProductResponse toResponse(Product entity);
}