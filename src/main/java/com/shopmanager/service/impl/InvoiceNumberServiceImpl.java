package com.shopmanager.service.impl;

import com.shopmanager.entity.InvoiceSequence;
import com.shopmanager.repository.InvoiceSequenceRepository;
import com.shopmanager.service.InvoiceNumberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Year;

@Service
@RequiredArgsConstructor
public class InvoiceNumberServiceImpl implements InvoiceNumberService {

    private static final String PREFIX = "SMS"; // Saurabh Mobile Shop

    private final InvoiceSequenceRepository repository;

    @Override
    @Transactional
    public synchronized String generateInvoiceNumber() {

        int currentYear = Year.now().getValue();

        InvoiceSequence sequence = repository.findByYear(currentYear)
                .orElseGet(() -> InvoiceSequence.builder()
                        .year(currentYear)
                        .lastNumber(0L)
                        .build());

        long nextNumber = sequence.getLastNumber() + 1;
        sequence.setLastNumber(nextNumber);

        repository.save(sequence);

        return formatInvoiceNumber(currentYear, nextNumber);
    }

    private String formatInvoiceNumber(int year, long number) {
        return String.format("%s-%d-%06d", PREFIX, year, number);
    }
}