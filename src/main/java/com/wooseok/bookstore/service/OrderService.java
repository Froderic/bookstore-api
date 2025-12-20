package com.wooseok.bookstore.service;

import com.wooseok.bookstore.dto.OrderDTO;

import java.util.List;

public interface OrderService {
    OrderDTO createOrder(OrderDTO orderDTO);
    OrderDTO getOrderById(Long orderId);
    List<OrderDTO> getCustomerOrders(Long customerId);
    List<OrderDTO> getAllOrders();
}