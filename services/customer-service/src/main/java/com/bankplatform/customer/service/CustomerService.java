package com.bankplatform.customer.service;

import com.bankplatform.customer.dto.CreateCustomerRequest;
import com.bankplatform.customer.dto.CustomerResponse;
import com.bankplatform.customer.entity.Customer;
import com.bankplatform.customer.entity.CustomerStatus;
import com.bankplatform.customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Un client avec cet email existe déjà");
        }

        Customer customer = Customer.builder()
                .userId(request.getUserId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .nationality(request.getNationality())
                .address(request.getAddress())
                .city(request.getCity())
                .country(request.getCountry())
                .operatorId(request.getOperatorId())
                .status(CustomerStatus.PENDING)
                .build();

        customer = customerRepository.save(customer);
        return CustomerResponse.from(customer);
    }

    public CustomerResponse getCustomerById(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client introuvable : " + id));
        return CustomerResponse.from(customer);
    }

    public CustomerResponse getCustomerByUserId(UUID userId) {
        Customer customer = customerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Client introuvable pour userId : " + userId));
        return CustomerResponse.from(customer);
    }

    public CustomerResponse getCustomerByEmail(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Client introuvable : " + email));
        return CustomerResponse.from(customer);
    }

    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(CustomerResponse::from)
                .collect(Collectors.toList());
    }

    public List<CustomerResponse> getCustomersByStatus(CustomerStatus status) {
        return customerRepository.findByStatus(status)
                .stream()
                .map(CustomerResponse::from)
                .collect(Collectors.toList());
    }

    public List<CustomerResponse> getCustomersByOperator(UUID operatorId) {
        return customerRepository.findByOperatorId(operatorId)
                .stream()
                .map(CustomerResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public CustomerResponse updateCustomerStatus(UUID id, CustomerStatus status) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client introuvable : " + id));
        customer.setStatus(status);
        return CustomerResponse.from(customerRepository.save(customer));
    }

    @Transactional
    public CustomerResponse updateCustomer(UUID id, CreateCustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client introuvable : " + id));

        if (request.getFirstName() != null)  customer.setFirstName(request.getFirstName());
        if (request.getLastName() != null)   customer.setLastName(request.getLastName());
        if (request.getPhoneNumber() != null) customer.setPhoneNumber(request.getPhoneNumber());
        if (request.getAddress() != null)    customer.setAddress(request.getAddress());
        if (request.getCity() != null)       customer.setCity(request.getCity());
        if (request.getCountry() != null)    customer.setCountry(request.getCountry());
        if (request.getNationality() != null) customer.setNationality(request.getNationality());

        return CustomerResponse.from(customerRepository.save(customer));
    }

    @Transactional
    public void verifyCustomer(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client introuvable : " + id));
        customer.setStatus(CustomerStatus.VERIFIED);
        customerRepository.save(customer);
    }

    @Transactional
    public void suspendCustomer(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client introuvable : " + id));
        customer.setStatus(CustomerStatus.SUSPENDED);
        customerRepository.save(customer);
    }
}
