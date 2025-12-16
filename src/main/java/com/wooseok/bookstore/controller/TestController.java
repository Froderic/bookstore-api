package com.wooseok.bookstore.controller;

import com.wooseok.bookstore.model.Book;
import com.wooseok.bookstore.model.Customer;
import com.wooseok.bookstore.repository.BookRepository;
import com.wooseok.bookstore.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @GetMapping("/setup")
    public Map<String, String> setupTestData() {
        // Create a test book
        Book book = Book.builder()
                .title("The Great Gatsby")
                .author("F. Scott Fitzgerald")
                .isbn("978-0743273565")
                .price(new BigDecimal("15.99"))
                .stock(50)
                .description("A classic American novel")
                .category("Fiction")
                .build();

        Book savedBook = bookRepository.save(book);

        // Create a test customer
        Customer customer = Customer.builder()
                .firstName("Woo")
                .lastName("Seok")
                .email("wooseok@example.com")
                .phone("+1234567890")
                .address("123 Main St, Montreal, QC")
                .build();

        Customer savedCustomer = customerRepository.save(customer);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Test data created successfully!");
        response.put("book", savedBook.getTitle() + " (ID: " + savedBook.getId() + ")");
        response.put("customer", savedCustomer.getFirstName() + " " + savedCustomer.getLastName() + " (ID: " + savedCustomer.getId() + ")");

        return response;
    }

    @GetMapping("/count")
    public Map<String, Long> getCounts() {
        Map<String, Long> counts = new HashMap<>();
        counts.put("books", bookRepository.count());
        counts.put("customers", customerRepository.count());
        return counts;
    }
}