package drinkshop.service;

import drinkshop.domain.Order;
import drinkshop.domain.OrderItem;
import drinkshop.domain.Product;
import drinkshop.repository.Repository;
import drinkshop.service.exception.BusinessException;
import drinkshop.service.exception.ErrorConstants;
import drinkshop.service.util.NullSafe;
import drinkshop.service.validator.OrderValidator;
import drinkshop.service.validator.Validator;

import java.util.List;
import java.util.Optional;

/**
 * OrderService - Manages all order-related business operations.
 * Includes dependency injection for OrderValidator (C06 - Input Validation).
 * Includes null-safe checks and Optional pattern (C05 - NullPointerException Prevention).
 * Includes custom error messages (C08 - Error Handling).
 */
public class OrderService {

    private final Repository<Integer, Order> orderRepo;
    private final Repository<Integer, Product> productRepo;
    private final Validator<Order> orderValidator;

    /**
     * Constructor with Dependency Injection
     * @param orderRepo Repository for order persistence
     * @param productRepo Repository for product lookup
     * @param orderValidator Validator for order validation
     */
    public OrderService(Repository<Integer, Order> orderRepo, 
                       Repository<Integer, Product> productRepo,
                       Validator<Order> orderValidator) {
        this.orderRepo = NullSafe.requireNonNull(orderRepo, ErrorConstants.NULL_REPOSITORY);
        this.productRepo = NullSafe.requireNonNull(productRepo, ErrorConstants.NULL_REPOSITORY);
        this.orderValidator = NullSafe.requireNonNull(orderValidator, ErrorConstants.NULL_VALIDATOR);
    }

    /**
     * Add new order with validation (C06 - Input Data Validation)
     * @param o Order to add
     * @throws BusinessException if order validation fails
     */
    public void addOrder(Order o) {
        // C06: Validate input data before processing
        orderValidator.validate(o);
        
        NullSafe.requireNonNull(o, String.format(ErrorConstants.NULL_ENTITY, "Order"));
        
        try {
            orderRepo.save(o);
        } catch (Exception e) {
            throw new BusinessException("ORDER_ADD_FAILED", 
                String.format(ErrorConstants.OPERATION_FAILED, e.getMessage()), e);
        }
    }

    /**
     * Update existing order with validation (C06 - Input Data Validation)
     * @param o Order to update
     * @throws BusinessException if validation fails or order not found
     */
    public void updateOrder(Order o) {
        // C06: Validate before update
        orderValidator.validate(o);
        
        NullSafe.requireNonNull(o, String.format(ErrorConstants.NULL_ENTITY, "Order"));
        
        // C05: Check if order exists
        Optional<Order> existing = Optional.ofNullable(orderRepo.findOne(o.getId()));
        if (existing.isEmpty()) {
            throw new BusinessException("ORDER_NOT_FOUND", 
                String.format(ErrorConstants.ORDER_NOT_FOUND, o.getId()));
        }
        
        try {
            orderRepo.update(o);
        } catch (Exception e) {
            throw new BusinessException("ORDER_UPDATE_FAILED", 
                String.format(ErrorConstants.OPERATION_FAILED, e.getMessage()), e);
        }
    }

    /**
     * Delete order with existence check (C05 - NullPointerException Prevention)
     * @param id Order ID
     * @throws BusinessException if order not found
     */
    public void deleteOrder(int id) {
        // C05: Check if order exists before deletion
        Optional<Order> order = Optional.ofNullable(orderRepo.findOne(id));
        
        if (order.isEmpty()) {
            throw new BusinessException("ORDER_NOT_FOUND", 
                String.format(ErrorConstants.ORDER_NOT_FOUND, id));
        }
        
        try {
            orderRepo.delete(id);
        } catch (Exception e) {
            throw new BusinessException("ORDER_DELETE_FAILED", 
                String.format(ErrorConstants.OPERATION_FAILED, e.getMessage()), e);
        }
    }

    /**
     * Get all orders (C05 - Null-safe handling)
     * @return List of all orders
     */
    public List<Order> getAllOrders() {
        try {
            List<Order> orders = orderRepo.findAll();
            // C05: Null-safe check
            return NullSafe.isNotEmpty(orders) ? orders : List.of();
        } catch (Exception e) {
            throw new BusinessException("ORDERS_FETCH_FAILED", 
                String.format(ErrorConstants.OPERATION_FAILED, e.getMessage()), e);
        }
    }

    /**
     * Find order by ID with null-safe handling (C05 - Optional Pattern)
     * @param id Order ID
     * @return Optional containing order if found
     */
    public Optional<Order> findById(int id) {
        try {
            return Optional.ofNullable(orderRepo.findOne(id));
        } catch (Exception e) {
            throw new BusinessException("ORDER_FIND_FAILED", 
                String.format(ErrorConstants.OPERATION_FAILED, e.getMessage()), e);
        }
    }

    /**
     * Compute total price for order with null-safe checks (C05 - NullPointerException Prevention)
     * @param o Order
     * @return Total price
     * @throws BusinessException if product not found or operation fails
     */
    public double computeTotal(Order o) {
        NullSafe.requireNonNull(o, String.format(ErrorConstants.NULL_ENTITY, "Order"));
        NullSafe.requireNonNull(o.getItems(), String.format(ErrorConstants.NULL_ENTITY, "Order items"));
        
        try {
            return o.getItems().stream()
                    .mapToDouble(item -> {
                        // C05: Null-safe product lookup with Optional
                        Optional<Product> product = Optional.ofNullable(
                            productRepo.findOne(item.getProduct().getId())
                        );
                        
                        if (product.isEmpty()) {
                            throw new BusinessException("PRODUCT_NOT_FOUND", 
                                String.format(ErrorConstants.PRODUCT_NOT_FOUND, item.getProduct().getId()));
                        }
                        
                        return product.get().getPret() * item.getQuantity();
                    })
                    .sum();
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("TOTAL_COMPUTATION_FAILED", 
                String.format(ErrorConstants.OPERATION_FAILED, e.getMessage()), e);
        }
    }

    /**
     * Add item to order with validation (C06 - Input Data Validation, C05 - Null checks)
     * @param o Order
     * @param item OrderItem to add
     * @throws BusinessException if validation fails or order not found
     */
    public void addItem(Order o, OrderItem item) {
        NullSafe.requireNonNull(o, String.format(ErrorConstants.NULL_ENTITY, "Order"));
        NullSafe.requireNonNull(item, String.format(ErrorConstants.NULL_ENTITY, "OrderItem"));
        
        // C05: Check if order exists
        Optional<Order> existing = Optional.ofNullable(orderRepo.findOne(o.getId()));
        if (existing.isEmpty()) {
            throw new BusinessException("ORDER_NOT_FOUND", 
                String.format(ErrorConstants.ORDER_NOT_FOUND, o.getId()));
        }
        
        try {
            o.getItems().add(item);
            orderRepo.update(o);
        } catch (Exception e) {
            throw new BusinessException("ORDER_ITEM_ADD_FAILED", 
                String.format(ErrorConstants.OPERATION_FAILED, e.getMessage()), e);
        }
    }

    /**
     * Remove item from order (C05 - Null-safe handling)
     * @param o Order
     * @param item OrderItem to remove
     * @throws BusinessException if operation fails
     */
    public void removeItem(Order o, OrderItem item) {
        NullSafe.requireNonNull(o, String.format(ErrorConstants.NULL_ENTITY, "Order"));
        NullSafe.requireNonNull(item, String.format(ErrorConstants.NULL_ENTITY, "OrderItem"));
        
        try {
            o.getItems().remove(item);
            orderRepo.update(o);
        } catch (Exception e) {
            throw new BusinessException("ORDER_ITEM_REMOVE_FAILED", 
                String.format(ErrorConstants.OPERATION_FAILED, e.getMessage()), e);
        }
    }
}