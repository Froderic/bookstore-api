package com.wooseok.bookstore.service;

import com.wooseok.bookstore.dto.OrderDTO;

import java.util.List;

public interface OrderService {
    OrderDTO createOrder(Long customerId, List<Long> bookIds, List<Integer> quantities);
    OrderDTO getOrderById(Long orderId);
    List<OrderDTO> getCustomerOrders(Long customerId);
}