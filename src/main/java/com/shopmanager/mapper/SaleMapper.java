package com.shopmanager.mapper;

import com.shopmanager.dto.sale.SaleItemResponse;
import com.shopmanager.dto.sale.SaleResponse;
import com.shopmanager.entity.Sale;
import com.shopmanager.entity.SaleItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface SaleMapper {

    @Mapping(target = "customerId", expression = "java(entity.getCustomer() != null ? entity.getCustomer().getId() : null)")
    @Mapping(target = "customerName", expression = "java(entity.getCustomer() != null ? entity.getCustomer().getName() : null)")
    @Mapping(target = "customerPhone", expression = "java(entity.getCustomer() != null ? entity.getCustomer().getPhone() : null)")
    @Mapping(target = "customerAddress", expression = "java(entity.getCustomer() != null ? entity.getCustomer().getAddress() : null)")
    @Mapping(target = "items", expression = "java(mapItemsList(entity.getItems()))")
    @Mapping(target = "createdAt", expression = "java(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)")
    SaleResponse toResponse(Sale entity);

    // Manual mapping for items to avoid setter issues
    default List<SaleItemResponse> mapItemsList(List<SaleItem> items) {
        if (items == null) return null;
        return items.stream().map(this::mapItem).collect(Collectors.toList());
    }

    default SaleItemResponse mapItem(SaleItem item) {
        if (item == null) return null;
        SaleItemResponse response = new SaleItemResponse();
        response.setId(item.getId());
        response.setItemName(item.getItemName());
        response.setType(item.getType());
        response.setQuantity(item.getQuantity());
        response.setUnitPrice(item.getUnitPrice());
        response.setLineTotal(item.getLineTotal());
        return response;
    }
}