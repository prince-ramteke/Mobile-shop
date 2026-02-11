package com.shopmanager.service.impl;

import com.shopmanager.dto.offer.OfferRequest;
import com.shopmanager.dto.offer.OfferResponse;
import com.shopmanager.entity.Offer;
import com.shopmanager.exception.ResourceNotFoundException;
import com.shopmanager.mapper.OfferMapper;
import com.shopmanager.repository.OfferRepository;
import com.shopmanager.service.OfferService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OfferServiceImpl implements OfferService {

    private final OfferRepository offerRepository;
    private final OfferMapper offerMapper;

    @Override
    public OfferResponse createOffer(OfferRequest request) {
        Offer offer = offerMapper.toEntity(request);
        offer.setActive(true);
        return offerMapper.toResponse(offerRepository.save(offer));
    }

    @Override
    public OfferResponse updateOffer(Long id, OfferRequest request) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found"));

        offer.setTitle(request.getTitle());
        offer.setDescription(request.getDescription());
        offer.setStartDate(request.getStartDate());
        offer.setEndDate(request.getEndDate());

        return offerMapper.toResponse(offerRepository.save(offer));
    }

    @Override
    public OfferResponse getOfferById(Long id) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found"));

        return offerMapper.toResponse(offer);
    }

    @Override
    public List<OfferResponse> getAllOffers() {
        return offerRepository.findAll()
                .stream()
                .map(offerMapper::toResponse)
                .toList();
    }

    @Override
    public List<OfferResponse> getActiveOffers() {
        return offerRepository.findByActiveTrue()
                .stream()
                .map(offerMapper::toResponse)
                .toList();
    }

    @Override
    public void deactivateOffer(Long id) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found"));

        offer.setActive(false);
        offerRepository.save(offer);
    }

    @Override
    public void deleteOffer(Long id) {
        Offer offer = offerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found"));

        offerRepository.delete(offer);
    }
}