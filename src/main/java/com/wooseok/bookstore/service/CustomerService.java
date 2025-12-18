package com.wooseok.bookstore.service;

import com.wooseok.bookstore.dto.CustomerDTO;
import java.util.List;

public interface CustomerService {

    CustomerDTO createCustomer(CustomerDTO customerDTO);

    CustomerDTO getCustomerById(Long id);

    List<CustomerDTO> getAllCustomers();

    CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO);

    void deleteCustomer(Long id);

    CustomerDTO findCustomerByEmail(String email);
}