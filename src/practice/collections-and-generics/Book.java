package dev.journi.practice.collections;

import java.util.Objects;

public record Book(String isbn, String title, String author) {
    public Book {
        if (isbn == null || isbn.isBlank()) {
            throw new IllegalArgumentException("ISBN is required");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title is required");
        }
        if (author == null || author.isBlank()) {
            throw new IllegalArgumentException("Author is required");
        }
        isbn = isbn.trim();
        title = title.trim();
        author = author.trim();
        Objects.requireNonNull(isbn);
    }
}
