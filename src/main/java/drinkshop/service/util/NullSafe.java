package drinkshop.service.util;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * NullSafe utility for safe null handling.
 * Provides methods to avoid NullPointerException and work with Optional pattern.
 */
public class NullSafe {
    
    /**
     * Checks if an object is null and throws exception with message
     */
    public static <T> T requireNonNull(T obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
        return obj;
    }
    
    /**
     * Safe get - returns empty Optional if null, otherwise Optional.of(value)
     */
    public static <T> Optional<T> of(T value) {
        return Optional.ofNullable(value);
    }
    
    /**
     * Safe null check - returns true if value is null
     */
    public static <T> boolean isNull(T value) {
        return value == null;
    }
    
    /**
     * Safe null check - returns true if value is not null
     */
    public static <T> boolean isNotNull(T value) {
        return value != null;
    }
    
    /**
     * Safe string check - returns true if string is null or empty
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || str.isBlank();
    }
    
    /**
     * Safe string check - returns true if string is not null and not empty
     */
    public static boolean isNotEmpty(String str) {
        return str != null && !str.isBlank();
    }
    
    /**
     * Safe collection check - returns true if list is null or empty
     */
    public static <T> boolean isNullOrEmpty(java.util.Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }
    
    /**
     * Safe collection check - returns true if list is not null and not empty
     */
    public static <T> boolean isNotEmpty(java.util.Collection<T> collection) {
        return collection != null && !collection.isEmpty();
    }
    
    /**
     * Safe value mapping
     */
    public static <T, R> Optional<R> map(T value, Function<T, R> mapper) {
        if (value == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(mapper.apply(value));
    }
    
    /**
     * Safe value action (side effect)
     */
    public static <T> void ifPresent(T value, Consumer<T> consumer) {
        if (value != null) {
            consumer.accept(value);
        }
    }
}

