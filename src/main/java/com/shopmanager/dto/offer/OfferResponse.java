package com.shopmanager.dto.offer;

import lombok.Data;

import java.time.LocalDate;

@Data
public class OfferResponse {

    private Long id;
    private String title;
    private String description;

    private LocalDate startDate;
    private LocalDate endDate;

    private Boolean active;
}