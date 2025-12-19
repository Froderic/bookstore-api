package com.wooseok.bookstore.controller;

import com.wooseok.bookstore.dto.BookDTO;
import com.wooseok.bookstore.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    // CRUD operations
    @PostMapping
    public ResponseEntity<BookDTO> createBook(@RequestBody BookDTO bookDTO) {
        BookDTO created = bookService.createBook(bookDTO);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<BookDTO>> getAllBooks() {
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDTO> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookDTO> updateBook(@PathVariable Long id, @RequestBody BookDTO bookDTO) {
        return ResponseEntity.ok(bookService.updateBook(id, bookDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    // Search and filter endpoints
    @GetMapping("/search/author/{author}")
    public ResponseEntity<List<BookDTO>> searchByAuthor(@PathVariable String author) {
        return ResponseEntity.ok(bookService.findBooksByAuthor(author));
    }

    @GetMapping("/search/title/{title}")
    public ResponseEntity<List<BookDTO>> searchByTitle(@PathVariable String title) {
        return ResponseEntity.ok(bookService.findBooksByTitle(title));
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<BookDTO>> getBooksByCategory(@PathVariable String category) {
        return ResponseEntity.ok(bookService.searchByCategory(category));
    }

    @GetMapping("/price-range")
    public ResponseEntity<List<BookDTO>> getBooksByPriceRange(
            @RequestParam BigDecimal minPrice,
            @RequestParam BigDecimal maxPrice) {
        return ResponseEntity.ok(bookService.findByPriceRange(minPrice, maxPrice));
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<BookDTO>> getLowStockBooks(
            @RequestParam(defaultValue = "10") int threshold) {
        return ResponseEntity.ok(bookService.findLowStockBooks(threshold));
    }
}