package dev.journi.practice.collections;

import java.util.List;
import java.util.Map;

public final class TestMain {
    public static void main(String[] args) {
        LibraryCatalog catalog = new LibraryCatalog();
        Book effectiveJava = new Book("978-0134685991", "Effective Java", "Joshua Bloch");
        Book javaConcurrency = new Book("978-0321349606", "Java Concurrency in Practice", "Brian Goetz");
        Book secondBlochBook = new Book("978-0321356680", "Java Puzzlers", "Joshua Bloch");

        require(catalog.add(effectiveJava), "The first ISBN must be added");
        require(!catalog.add(new Book(effectiveJava.isbn(), "Duplicate", "Someone Else")),
                "A duplicate ISBN must be rejected");
        require(catalog.add(javaConcurrency), "A distinct ISBN must be added");
        require(catalog.add(secondBlochBook), "A second distinct ISBN must be added");
        require(catalog.findByIsbn(effectiveJava.isbn()).orElseThrow().equals(effectiveJava),
                "Lookup must return the stored book");
        require(catalog.findByIsbn("missing").isEmpty(), "Missing ISBN lookup must return Optional.empty");

        Map<String, List<Book>> grouped = catalog.groupByAuthor();
        require(grouped.get("Joshua Bloch").equals(List.of(effectiveJava, secondBlochBook)),
                "Grouping must retain deterministic insertion order");
        require(grouped.get("Brian Goetz").equals(List.of(javaConcurrency)),
                "Grouping must contain every author");
        try {
            grouped.put("Mutable", List.of());
            throw new AssertionError("The grouped map must be read-only");
        } catch (UnsupportedOperationException expected) {
            // Expected contract.
        }
    }

    private static void require(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
