package dev.journi.practice.collections;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class LibraryCatalog {
    private final Map<String, Book> booksByIsbn = new LinkedHashMap<>();

    public boolean add(Book book) {
        return booksByIsbn.putIfAbsent(book.isbn(), book) == null;
    }

    public Optional<Book> findByIsbn(String isbn) {
        return Optional.ofNullable(booksByIsbn.get(isbn));
    }

    public Map<String, List<Book>> groupByAuthor() {
        Map<String, List<Book>> grouped = new LinkedHashMap<>();
        booksByIsbn.values().forEach(book -> grouped
                .computeIfAbsent(book.author(), ignored -> new ArrayList<>())
                .add(book));
        grouped.replaceAll((author, books) -> List.copyOf(books));
        return Map.copyOf(grouped);
    }
}
