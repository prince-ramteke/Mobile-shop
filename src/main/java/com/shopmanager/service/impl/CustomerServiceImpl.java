package com.shopmanager.service.impl;

import com.shopmanager.dto.customer.CustomerRequest;
import com.shopmanager.dto.customer.CustomerResponse;
import com.shopmanager.entity.Customer;
import com.shopmanager.exception.DuplicateEntryException;
import com.shopmanager.exception.ResourceNotFoundException;
import com.shopmanager.mapper.CustomerMapper;
import com.shopmanager.repository.CustomerRepository;
import com.shopmanager.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public CustomerResponse createCustomer(CustomerRequest request) {

        if (customerRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateEntryException("Phone number already exists");
        }

        Customer customer = customerMapper.toEntity(request);
        return customerMapper.toResponse(customerRepository.save(customer));
    }

    @Override
    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {

        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        if (!customer.getPhone().equals(request.getPhone())
                && customerRepository.existsByPhone(request.getPhone())) {
            throw new DuplicateEntryException("Phone number already exists");
        }

        customer.setName(request.getName());
        customer.setPhone(request.getPhone());
        customer.setWhatsappNumber(
                request.getWhatsappNumber() != null ? request.getWhatsappNumber() : request.getPhone()
        );
        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());

        return customerMapper.toResponse(customerRepository.save(customer));
    }

    @Override
    public CustomerResponse getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
        return customerMapper.toResponse(customer);
    }


    @Override
    public Page<CustomerResponse> search(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        if (query == null || query.trim().isEmpty()) {
            return customerRepository.findAllOrderByCreatedAtDesc(pageable)
                    .map(customerMapper::toResponse);
        }

        Page<Customer> customers = customerRepository.searchCustomers(query, pageable);
        return customers.map(customerMapper::toResponse);
    }


    @Override
    public void deleteCustomer(Long id) {
        if (!customerRepository.existsById(id))
            throw new ResourceNotFoundException("Customer not found");
        customerRepository.deleteById(id);
    }
}