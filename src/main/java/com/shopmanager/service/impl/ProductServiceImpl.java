package com.shopmanager.service.impl;

import com.shopmanager.dto.product.ProductRequest;
import com.shopmanager.dto.product.ProductResponse;
import com.shopmanager.entity.Product;

import com.shopmanager.exception.ResourceNotFoundException;
import com.shopmanager.mapper.ProductMapper;
import com.shopmanager.repository.ProductRepository;
import com.shopmanager.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        Product product = productMapper.toEntity(request);
        return productMapper.toResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {

        Product existing = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        existing.setName(request.getName());
        existing.setBrand(request.getBrand());
        existing.setCategory(request.getCategory());
        existing.setImei(request.getImei());
        existing.setSellingPrice(request.getSellingPrice());
        existing.setCostPrice(request.getCostPrice());
        existing.setStock(request.getStock());

        return productMapper.toResponse(productRepository.save(existing));
    }

    @Override
    public ProductResponse getProductById(Long id) {
        return productRepository.findById(id)
                .map(productMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    @Override
    public Page<ProductResponse> searchProducts(String query, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Product> results;

        if (query == null || query.isBlank()) {
            results = productRepository.findAll(pageable);
        } else {
            results = productRepository.searchProducts(query, pageable);
        }

        return results.map(productMapper::toResponse);
    }

    @Override
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found");
        }
        productRepository.deleteById(id);
    }
}