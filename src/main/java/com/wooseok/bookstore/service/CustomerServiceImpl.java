package com.wooseok.bookstore.service;

import com.wooseok.bookstore.dto.CustomerDTO;
import com.wooseok.bookstore.exception.ResourceNotFoundException;
import com.wooseok.bookstore.model.Customer;
import com.wooseok.bookstore.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        // Check if email already exists
        if (customerRepository.findByEmail(customerDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + customerDTO.getEmail());
        }

        Customer customer = mapToEntity(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return mapToDTO(savedCustomer);
    }

    @Override
    public CustomerDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        return mapToDTO(customer);
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));

        // Check if new email already exists (and it's not this customer's current email)
        if (!customer.getEmail().equals(customerDTO.getEmail()) &&
                customerRepository.findByEmail(customerDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists: " + customerDTO.getEmail());
        }

        // Update fields
        customer.setFirstName(customerDTO.getFirstName());
        customer.setLastName(customerDTO.getLastName());
        customer.setEmail(customerDTO.getEmail());
        customer.setPhoneNumber(customerDTO.getPhoneNumber());
        customer.setAddress(customerDTO.getAddress());

        Customer updatedCustomer = customerRepository.save(customer);
        return mapToDTO(updatedCustomer);
    }

    @Override
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        customerRepository.delete(customer);
    }

    @Override
    public CustomerDTO findCustomerByEmail(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "email", email));
        return mapToDTO(customer);
    }

    // Helper methods to convert between Entity and DTO
    private CustomerDTO mapToDTO(Customer customer) {
        return CustomerDTO.builder()
                .id(customer.getId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .address(customer.getAddress())
                .build();
    }

    private Customer mapToEntity(CustomerDTO customerDTO) {
        return Customer.builder()
                .firstName(customerDTO.getFirstName())
                .lastName(customerDTO.getLastName())
                .email(customerDTO.getEmail())
                .phoneNumber(customerDTO.getPhoneNumber())
                .address(customerDTO.getAddress())
                .build();
    }
}