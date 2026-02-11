package com.shopmanager.controller.offer;

import com.shopmanager.dto.offer.OfferRequest;
import com.shopmanager.dto.offer.OfferResponse;
import com.shopmanager.service.OfferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/offers")
@RequiredArgsConstructor
public class OfferController {

    private final OfferService offerService;

    @PostMapping
    public ResponseEntity<OfferResponse> createOffer(
            @Valid @RequestBody OfferRequest request) {
        return ResponseEntity.ok(offerService.createOffer(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OfferResponse> updateOffer(
            @PathVariable Long id,
            @Valid @RequestBody OfferRequest request) {
        return ResponseEntity.ok(offerService.updateOffer(id, request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OfferResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(offerService.getOfferById(id));
    }

    @GetMapping
    public ResponseEntity<List<OfferResponse>> listOffers() {
        return ResponseEntity.ok(offerService.getAllOffers());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffer(@PathVariable Long id) {
        offerService.deleteOffer(id);
        return ResponseEntity.noContent().build();
    }
}