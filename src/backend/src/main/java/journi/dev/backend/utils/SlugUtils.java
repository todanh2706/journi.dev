package journi.dev.backend.utils;

import java.text.Normalizer;
import java.util.Locale;

public final class SlugUtils {
    private SlugUtils() {
    }

    public static String toSlug(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }

        String normalizedValue = Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");

        return normalizedValue.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-+|-+$", "");
    }
}
