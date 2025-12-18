package com.wooseok.bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemDTO {
    private Long bookId;
    private String bookTitle;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal; // price * quantity
}