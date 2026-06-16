package journi.dev.backend.entities;

import java.util.Locale;

import journi.dev.backend.exceptions.BadRequestException;

public enum NodeType {
    LESSON,
    PRACTICE,
    PROJECT,
    QUIZ,
    CHALLENGE;

    public static NodeType from(String value) {
        if (value == null || value.isBlank()) {
            throw new BadRequestException("Node type is required");
        }

        String normalizedValue = value.trim()
                .replace('-', '_')
                .replace(' ', '_')
                .toUpperCase(Locale.ROOT);

        try {
            return NodeType.valueOf(normalizedValue);
        } catch (IllegalArgumentException exception) {
            throw new BadRequestException("Unsupported node type: " + value);
        }
    }
}
