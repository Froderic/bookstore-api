package com.wooseok.bookstore.service;

import com.wooseok.bookstore.dto.BookDTO;
import com.wooseok.bookstore.exception.ResourceNotFoundException;
import com.wooseok.bookstore.model.Book;
import com.wooseok.bookstore.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Override
    public BookDTO createBook(BookDTO bookDTO) {
        Book book = mapToEntity(bookDTO);
        Book savedBook = bookRepository.save(book);
        return mapToDTO(savedBook);
    }

    @Override
    public BookDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
        return mapToDTO(book);
    }

    @Override
    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));

        // Update fields
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setIsbn(bookDTO.getIsbn());
        book.setPrice(bookDTO.getPrice());
        book.setStockQuantity(bookDTO.getStockQuantity());
        book.setDescription(bookDTO.getDescription());

        Book updatedBook = bookRepository.save(book);
        return mapToDTO(updatedBook);
    }

    @Override
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
        bookRepository.delete(book);
    }

    @Override
    public List<BookDTO> findBooksByAuthor(String author) {
        return bookRepository.findByAuthorContainingIgnoreCase(author).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookDTO> findBooksByTitle(String title) {
        return bookRepository.findByTitleContainingIgnoreCase(title).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookDTO> searchByCategory(String category) {
        List<Book> books = bookRepository.findByCategory(category);
        return books.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookDTO> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("Minimum price cannot be greater than maximum price");
        }
        List<Book> books = bookRepository.findByPriceBetween(minPrice, maxPrice);
        return books.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookDTO> findLowStockBooks(int threshold) {
        if (threshold < 0) {
            throw new IllegalArgumentException("Stock threshold cannot be negative");
        }
        List<Book> books = bookRepository.findByStockQuantityLessThan(threshold);
        return books.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    // Helper methods to convert between Entity and DTO
    private BookDTO mapToDTO(Book book) {
        return BookDTO.builder()
                .id(book.getId())
                .title(book.getTitle())
                .author(book.getAuthor())
                .isbn(book.getIsbn())
                .category(book.getCategory())
                .price(book.getPrice())
                .stockQuantity(book.getStockQuantity())
                .description(book.getDescription())
                .build();
    }

    private Book mapToEntity(BookDTO bookDTO) {
        return Book.builder()
                .title(bookDTO.getTitle())
                .author(bookDTO.getAuthor())
                .isbn(bookDTO.getIsbn())
                .category(bookDTO.getCategory())
                .price(bookDTO.getPrice())
                .stockQuantity(bookDTO.getStockQuantity())
                .description(bookDTO.getDescription())
                .build();
    }
}