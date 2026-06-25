package dev.journi.practice.collections;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class LibraryCatalog {
    public boolean add(Book book) {
        return false;
    }

    public Optional<Book> findByIsbn(String isbn) {
        return Optional.empty();
    }

    public Map<String, List<Book>> groupByAuthor() {
        return Map.of();
    }
}
