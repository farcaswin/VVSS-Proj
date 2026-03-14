package drinkshop.service.exception;

/**
 * Custom Business Exception for application-specific errors
 * Used for validation errors, entity not found, duplicate entities, etc.
 */
public class BusinessException extends RuntimeException {

    private String errorCode;
    private Object[] parameters;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(String errorCode, String message, Object... parameters) {
        super(message);
        this.errorCode = errorCode;
        this.parameters = parameters;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public Object[] getParameters() {
        return parameters;
    }
}

