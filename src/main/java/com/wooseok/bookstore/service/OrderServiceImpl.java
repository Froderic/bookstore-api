package com.wooseok.bookstore.service;

import com.wooseok.bookstore.dto.OrderDTO;
import com.wooseok.bookstore.dto.OrderItemDTO;
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
    public OrderDTO createOrder(Long customerId, List<Long> bookIds, List<Integer> quantities) {
        // Validate customer exists
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", customerId));

        // Validate bookIds and quantities lists match
        if (bookIds.size() != quantities.size()) {
            throw new IllegalArgumentException("Book IDs and quantities lists must have the same size");
        }

        // Create the order
        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus(Order.OrderStatus.PENDING);

        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        // Process each book in the order
        for (int i = 0; i < bookIds.size(); i++) {
            Long bookId = bookIds.get(i);
            Integer quantity = quantities.get(i);

            // Validate quantity
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than 0 for book ID: " + bookId);
            }

            // Find the book
            Book book = bookRepository.findById(bookId)
                    .orElseThrow(() -> new ResourceNotFoundException("Book", "id", bookId));

            // Check stock availability
            if (book.getStockQuantity() < quantity) {
                throw new IllegalArgumentException(
                        "Insufficient stock for book: " + book.getTitle() +
                                ". Available: " + book.getStockQuantity() + ", Requested: " + quantity);
            }

            // Reduce stock
            book.setStockQuantity(book.getStockQuantity() - quantity);
            bookRepository.save(book);

            // Create order item
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setBook(book);
            orderItem.setQuantity(quantity);
            orderItem.setPrice(book.getPrice());

            orderItems.add(orderItem);

            // Calculate subtotal and add to total
            BigDecimal subtotal = book.getPrice().multiply(BigDecimal.valueOf(quantity));
            totalAmount = totalAmount.add(subtotal);
        }

        order.setOrderItems(orderItems);
        order.setTotalAmount(totalAmount);

        // Save the order (cascades to order items)
        Order savedOrder = orderRepository.save(order);

        return mapToDTO(savedOrder);
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