package com.shopmanager.service;

import com.shopmanager.dto.product.ProductRequest;
import com.shopmanager.dto.product.ProductResponse;
import org.springframework.data.domain.Page;

public interface ProductService {

    ProductResponse createProduct(ProductRequest request);

    ProductResponse updateProduct(Long id, ProductRequest request);

    ProductResponse getProductById(Long id);

    Page<ProductResponse> searchProducts(String query, int page, int size);

    void deleteProduct(Long id);
}