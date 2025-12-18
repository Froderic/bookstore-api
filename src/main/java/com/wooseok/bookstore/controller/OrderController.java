package com.wooseok.bookstore.controller;

import com.wooseok.bookstore.dto.OrderDTO;
import com.wooseok.bookstore.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    // Place a new order
    @PostMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody CreateOrderRequest request) {
        OrderDTO order = orderService.createOrder(
                request.getCustomerId(),
                request.getBookIds(),
                request.getQuantities()
        );
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    // Get order by ID
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        OrderDTO order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    // Get all orders for a specific customer
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderDTO>> getCustomerOrders(@PathVariable Long customerId) {
        List<OrderDTO> orders = orderService.getCustomerOrders(customerId);
        return ResponseEntity.ok(orders);
    }

    // Inner class for the request body
    @lombok.Data
    public static class CreateOrderRequest {
        private Long customerId;
        private List<Long> bookIds;
        private List<Integer> quantities;
    }
}