package com.wooseok.bookstore.service;

import com.wooseok.bookstore.dto.BookDTO;

import java.math.BigDecimal;
import java.util.List;

public interface BookService {

    BookDTO createBook(BookDTO bookDTO);

    BookDTO getBookById(Long id);

    List<BookDTO> getAllBooks();

    BookDTO updateBook(Long id, BookDTO bookDTO);

    void deleteBook(Long id);

    List<BookDTO> findBooksByAuthor(String author);

    List<BookDTO> findBooksByTitle(String title);


    List<BookDTO> searchByCategory(String category);
    List<BookDTO> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);
    List<BookDTO> findLowStockBooks(int threshold);
}