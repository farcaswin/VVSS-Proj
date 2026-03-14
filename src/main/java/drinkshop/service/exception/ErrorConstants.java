package drinkshop.service.exception;

/**
 * Centralized error messages for the entire application.
 * All error messages are in English and follow a consistent pattern.
 */
public class ErrorConstants {
    private ErrorConstants() {
        /* This utility class should not be instantiated */
    }

    
    // ===================== VALIDATION ERRORS =====================
    public static final String INVALID_ID = "Invalid ID: ID must be greater than 0";
    public static final String INVALID_NAME = "Invalid name: Name cannot be null or empty";
    public static final String INVALID_PRICE = "Invalid price: Price must be greater than 0";
    public static final String INVALID_QUANTITY = "Invalid quantity: Quantity must be greater than 0";
    public static final String INVALID_DESCRIPTION = "Invalid description: Description cannot be null or empty";
    
    // ===================== PRODUCT ERRORS =====================
    public static final String PRODUCT_NOT_FOUND = "Product not found with ID: %d";
    public static final String PRODUCT_ALREADY_EXISTS = "Product already exists with ID: %d";
    public static final String PRODUCT_INVALID_CATEGORY = "Invalid product category";
    public static final String PRODUCT_INVALID_TYPE = "Invalid product type";
    
    // ===================== ORDER ERRORS =====================
    public static final String ORDER_NOT_FOUND = "Order not found with ID: %d";
    public static final String ORDER_ALREADY_EXISTS = "Order already exists with ID: %d";
    public static final String ORDER_EMPTY_ITEMS = "Order must contain at least one item";
    public static final String ORDER_CANNOT_DELETE_COMPLETED = "Cannot delete a completed order";
    
    // ===================== ORDER ITEM ERRORS =====================
    public static final String ORDER_ITEM_INVALID_QUANTITY = "Order item quantity must be greater than 0";
    public static final String ORDER_ITEM_PRODUCT_NOT_FOUND = "Product not found for order item";
    
    // ===================== RECIPE ERRORS =====================
    public static final String RECIPE_NOT_FOUND = "Recipe not found with ID: %d";
    public static final String RECIPE_ALREADY_EXISTS = "Recipe already exists with ID: %d";
    public static final String RECIPE_EMPTY_INGREDIENTS = "Recipe must contain at least one ingredient";
    public static final String RECIPE_INVALID_INGREDIENT = "Invalid ingredient in recipe";
    
    // ===================== RECIPE INGREDIENT ERRORS =====================
    public static final String RECIPE_INGREDIENT_NOT_FOUND = "Recipe ingredient not found with ID: %d";
    public static final String RECIPE_INGREDIENT_INVALID_QUANTITY = "Recipe ingredient quantity must be greater than 0";
    
    // ===================== STOCK ERRORS =====================
    public static final String STOCK_NOT_FOUND = "Stock not found with ID: %d";
    public static final String STOCK_ALREADY_EXISTS = "Stock already exists for product ID: %d";
    public static final String STOCK_INSUFFICIENT = "Insufficient stock: Required %d but available %d";
    public static final String STOCK_INVALID_QUANTITY = "Stock quantity must be greater than or equal to 0";
    
    // ===================== NULL POINTER ERRORS =====================
    public static final String NULL_ENTITY = "%s cannot be null";
    public static final String NULL_REPOSITORY = "Repository cannot be null";
    public static final String NULL_VALIDATOR = "Validator cannot be null";
    
    // ===================== GENERAL ERRORS =====================
    public static final String OPERATION_FAILED = "Operation failed: %s";
    public static final String UNEXPECTED_ERROR = "Unexpected error occurred: %s";
}

