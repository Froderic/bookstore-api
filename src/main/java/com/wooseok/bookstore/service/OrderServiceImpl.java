package com.wooseok.bookstore.service;

import com.wooseok.bookstore.dto.OrderDTO;
import com.wooseok.bookstore.dto.OrderItemDTO;
import com.wooseok.bookstore.exception.OrderValidationException;
import com.wooseok.bookstore.exception.ResourceNotFoundException;
import com.wooseok.bookstore.model.Book;
import com.wooseok.bookstore.model.Customer;
import com.wooseok.bookstore.model.Order;
import com.wooseok.bookstore.model.OrderItem;
import com.wooseok.bookstore.repository.BookRepository;
import com.wooseok.bookstore.repository.CustomerRepository;
import com.wooseok.bookstore.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final BookRepository bookRepository;

    @Override
    public OrderDTO createOrder(OrderDTO orderDTO) {
        // Validate customer exists
        Customer customer = customerRepository.findById(orderDTO.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", orderDTO.getCustomerId()));

        // Validate cart is not empty
        if (orderDTO.getItems() == null || orderDTO.getItems().isEmpty()) {
            throw new OrderValidationException("Cannot create order with empty cart");
        }

        // Create the order
        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus(Order.OrderStatus.PENDING);

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        // Process each item in the order
        for (OrderItemDTO itemDTO : orderDTO.getItems()) {
            // Validate quantity
            if (itemDTO.getQuantity() <= 0) {
                throw new OrderValidationException("Quantity must be greater than 0 for book ID: " + itemDTO.getBookId());
            }

            // Find the book
            Book book = bookRepository.findById(itemDTO.getBookId())
                    .orElseThrow(() -> new ResourceNotFoundException("Book", "id", itemDTO.getBookId()));

            // Check stock availability
            if (book.getStockQuantity() < itemDTO.getQuantity()) {
                throw new IllegalArgumentException(
                        "Insufficient stock for book: " + book.getTitle() +
                                ". Available: " + book.getStockQuantity() + ", Requested: " + itemDTO.getQuantity());
            }

            // Reduce stock
            book.setStockQuantity(book.getStockQuantity() - itemDTO.getQuantity());
            bookRepository.save(book);

            // Create order item
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBook(book);
            orderItem.setQuantity(itemDTO.getQuantity());
            orderItem.setPrice(book.getPrice());

            orderItems.add(orderItem);

            // Calculate subtotal and add to total
            BigDecimal subtotal = book.getPrice().multiply(BigDecimal.valueOf(itemDTO.getQuantity()));
            totalAmount = totalAmount.add(subtotal);
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);

        // Save the order (cascades to order items)
        Order savedOrder = orderRepository.save(order);

        return mapToDTO(savedOrder);
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));
        return mapToDTO(order);
    }

    @Override
    public List<OrderDTO> getCustomerOrders(Long customerId) {
        // Validate customer exists
        customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        List<Order> orders = orderRepository.findByCustomerId(customerId);
        return orders.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Helper method to convert Order entity to DTO
    private OrderDTO mapToDTO(Order order) {
        List<OrderItemDTO> itemDTOs = order.getOrderItems().stream()
                .map(item -> OrderItemDTO.builder()
                        .bookId(item.getBook().getId())
                        .bookTitle(item.getBook().getTitle())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .subtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .build())
                .collect(Collectors.toList());

        return OrderDTO.builder()
                .id(order.getId())
                .customerId(order.getCustomer().getId())
                .customerName(order.getCustomer().getFirstName() + " " + order.getCustomer().getLastName())
                .items(itemDTOs)
                .totalAmount(order.getTotalAmount())
                .orderDate(order.getOrderDate())
                .build();
    }
}