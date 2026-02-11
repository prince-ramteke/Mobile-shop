package com.shopmanager.mapper;

import com.shopmanager.dto.repair.RepairJobRequest;
import com.shopmanager.dto.repair.RepairJobResponse;
import com.shopmanager.entity.RepairJob;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface RepairJobMapper {

    @Mapping(target = "customerId", expression = "java(entity.getCustomer() != null ? entity.getCustomer().getId() : null)")
    @Mapping(target = "customerName", expression = "java(entity.getCustomer() != null ? entity.getCustomer().getName() : null)")
    @Mapping(target = "customerPhone", expression = "java(entity.getCustomer() != null ? entity.getCustomer().getPhone() : null)")
    @Mapping(target = "createdAt", expression = "java(entity.getCreatedAt() != null ? entity.getCreatedAt().toString() : null)")
    RepairJobResponse toResponse(RepairJob entity);

    List<RepairJobResponse> toResponseList(List<RepairJob> entities);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "customer", ignore = true)
    @Mapping(target = "jobNumber", ignore = true)
    @Mapping(target = "pendingAmount", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    RepairJob toEntity(RepairJobRequest request);
}