package com.shopmanager.mapper;

import com.shopmanager.dto.message.MessageLogRequest;
import com.shopmanager.dto.message.MessageLogResponse;
import com.shopmanager.entity.MessageLog;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface MessageLogMapper {

    @Mapping(target = "customer.id", source = "customerId")
    MessageLog toEntity(MessageLogRequest request);

    @Mapping(target = "customerId", source = "customer.id")
    @Mapping(target = "sentAt", expression = "java(entity.getSentAt() != null ? entity.getSentAt().toString() : null)")
    MessageLogResponse toResponse(MessageLog entity);
}