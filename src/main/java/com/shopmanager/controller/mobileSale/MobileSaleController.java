package com.shopmanager.controller.mobileSale;

import com.shopmanager.dto.mobileSale.MobileSaleRequest;
import com.shopmanager.service.MobileSaleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mobile-sales")
@RequiredArgsConstructor
public class MobileSaleController {

    private final MobileSaleService saleService;

    @PostMapping
    public Long create(@RequestBody MobileSaleRequest request) {
        return saleService.createSale(request);
    }
}