package com.wooseok.bookstore.repository;

import com.wooseok.bookstore.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    // Custom query methods - Spring generates SQL automatically!

    Optional<Book> findByIsbn(String isbn);

    List<Book> findByCategory(String category);

    List<Book> findByAuthor(String author);

    List<Book> findByTitleContainingIgnoreCase(String title);

    List<Book> findByCategoryAndStockQuantityGreaterThan(String category, Integer stockQuantity);

    List<Book> findByAuthorContainingIgnoreCase(String author);

    List<Book> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    List<Book> findByStockQuantityLessThan(int threshold);
}