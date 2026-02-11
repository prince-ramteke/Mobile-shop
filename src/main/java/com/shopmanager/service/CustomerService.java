package com.shopmanager.service;

import com.shopmanager.dto.customer.CustomerRequest;
import com.shopmanager.dto.customer.CustomerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomerService {

    CustomerResponse createCustomer(CustomerRequest request);

    CustomerResponse updateCustomer(Long id, CustomerRequest request);

    CustomerResponse getCustomerById(Long id);

    Page<CustomerResponse> search(String query, int page, int size);

    void deleteCustomer(Long id);
}