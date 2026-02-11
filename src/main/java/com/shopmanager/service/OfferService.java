package com.shopmanager.service;

import com.shopmanager.dto.offer.OfferRequest;
import com.shopmanager.dto.offer.OfferResponse;

import java.util.List;

public interface OfferService {

    OfferResponse createOffer(OfferRequest request);

    OfferResponse updateOffer(Long id, OfferRequest request);

    OfferResponse getOfferById(Long id);

    List<OfferResponse> getAllOffers();

    List<OfferResponse> getActiveOffers();

    void deactivateOffer(Long id);

    void deleteOffer(Long id);
}