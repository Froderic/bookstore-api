package com.wooseok.bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long id;
    private Long customerId;
    private String customerName;
    private List<OrderItemDTO> items;
    private BigDecimal totalAmount;
    private LocalDateTime orderDate;
}