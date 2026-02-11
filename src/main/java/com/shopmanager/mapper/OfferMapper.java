package com.shopmanager.mapper;

import com.shopmanager.dto.offer.OfferRequest;
import com.shopmanager.dto.offer.OfferResponse;
import com.shopmanager.entity.Offer;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OfferMapper {

    Offer toEntity(OfferRequest dto);

    OfferResponse toResponse(Offer entity);
}