package com.wooseok.bookstore.controller;

import com.wooseok.bookstore.dto.OrderDTO;
import com.wooseok.bookstore.dto.OrderItemDTO;
import com.wooseok.bookstore.service.OrderService;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
        // Convert CreateOrderRequest to OrderDTO for the service layer
        OrderDTO orderDTO = OrderDTO.builder()
                .customerId(request.getCustomerId())
                .items(request.getItems())
                .build();

        OrderDTO order = orderService.createOrder(orderDTO);
        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<OrderDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
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
        @NotNull(message = "Customer ID is required")
        private Long customerId;

        @NotEmpty(message = "Order must contain at least one item")
        private List<OrderItemDTO> items;
    }
}