package drinkshop.service;

import drinkshop.domain.BeverageCategory;
import drinkshop.domain.BeverageType;
import drinkshop.domain.Product;
import drinkshop.repository.Repository;
import drinkshop.service.exception.BusinessException;
import drinkshop.service.exception.ErrorConstants;
import drinkshop.service.util.NullSafe;
import drinkshop.service.validator.Validator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ProductService - Manages all product-related business operations.
 * Includes dependency injection for ProductValidator (C06 - Input Validation).
 * Includes null-safe checks and Optional pattern (C05 - NullPointerException Prevention).
 */
public class ProductService {

    private final Repository<Integer, Product> productRepo;
    private final Validator<Product> productValidator;

    /**
     * Constructor with Dependency Injection
     *
     * @param productRepo      Repository for product persistence
     * @param productValidator Validator for product validation
     */
    public ProductService(Repository<Integer, Product> productRepo, Validator<Product> productValidator) {
        this.productRepo = NullSafe.requireNonNull(productRepo, ErrorConstants.NULL_REPOSITORY);
        this.productValidator = NullSafe.requireNonNull(productValidator, ErrorConstants.NULL_VALIDATOR);
    }

    /**
     * Add new product with validation (C06 - Input Data Validation)
     *
     * @param p Product to add
     * @throws BusinessException if product validation fails
     */
    public void addProduct(Product p) {
        // C06: Validate input data before processing
        productValidator.validate(p);

        // C05: Null-safe check
        NullSafe.requireNonNull(p, String.format(ErrorConstants.NULL_ENTITY, "Product"));

        try {
            productRepo.save(p);
        } catch (Exception e) {
            throw new BusinessException("PRODUCT_ADD_FAILED",
                    String.format(ErrorConstants.OPERATION_FAILED, e.getMessage()), e);
        }
    }

    /**
     * Update existing product with validation (C06 - Input Data Validation)
     *
     * @param id        Product ID
     * @param name      Product name
     * @param price     Product price
     * @param categorie Product category
     * @param tip       Product type
     * @throws BusinessException if validation fails or product not found
     */
    public void updateProduct(int id, String name, double price, BeverageCategory categorie, BeverageType tip) {
        // C05: Null-safe checks
        NullSafe.requireNonNull(name, String.format(ErrorConstants.NULL_ENTITY, "Product name"));
        NullSafe.requireNonNull(categorie, String.format(ErrorConstants.NULL_ENTITY, "Category"));
        NullSafe.requireNonNull(tip, String.format(ErrorConstants.NULL_ENTITY, "Type"));

        // Check if product exists (C05 - Avoid NullPointerException)
        Product existing = productRepo.findOne(id);
        if (existing == null) {
            throw new BusinessException("PRODUCT_NOT_FOUND",
                    String.format(ErrorConstants.PRODUCT_NOT_FOUND, id));
        }

        Product updated = new Product(id, name, price, categorie, tip);

        // C06: Validate before update
        productValidator.validate(updated);

        try {
            productRepo.update(updated);
        } catch (Exception e) {
            throw new BusinessException("PRODUCT_UPDATE_FAILED",
                    String.format(ErrorConstants.OPERATION_FAILED, e.getMessage()), e);
        }
    }

    /**
     * Delete product with existence check (C05 - NullPointerException Prevention)
     *
     * @param id Product ID
     * @throws BusinessException if product not found
     */
    public void deleteProduct(int id) {
        // C05: Check if product exists before deletion
        Optional<Product> product = Optional.ofNullable(productRepo.findOne(id));

        if (product.isEmpty()) {
            throw new BusinessException("PRODUCT_NOT_FOUND",
                    String.format(ErrorConstants.PRODUCT_NOT_FOUND, id));
        }

        try {
            productRepo.delete(id);
        } catch (Exception e) {
            throw new BusinessException("PRODUCT_DELETE_FAILED",
                    String.format(ErrorConstants.OPERATION_FAILED, e.getMessage()), e);
        }
    }

    /**
     * Get all products (C05 - Null-safe handling)
     *
     * @return List of all products
     */
    public List<Product> getAllProducts() {
        try {
            List<Product> products = productRepo.findAll();
            // C05: Null-safe check
            return NullSafe.isNotEmpty(products) ? products : List.of();
        } catch (Exception e) {
            throw new BusinessException("PRODUCTS_FETCH_FAILED",
                    String.format(ErrorConstants.OPERATION_FAILED, e.getMessage()), e);
        }
    }

    /**
     * Find product by ID with null-safe handling (C05 - Optional Pattern)
     *
     * @param id Product ID
     * @return Optional containing product if found
     */
    public Optional<Product> findById(int id) {
        try {
            return Optional.ofNullable(productRepo.findOne(id));
        } catch (Exception e) {
            throw new BusinessException("PRODUCT_FIND_FAILED",
                    String.format(ErrorConstants.OPERATION_FAILED, e.getMessage()), e);
        }
    }

    /**
     * Filter products by category with null-safe handling
     *
     * @param categorie Category filter
     * @return List of filtered products
     */
    public List<Product> filterByCategorie(BeverageCategory categorie) {
        NullSafe.requireNonNull(categorie, String.format(ErrorConstants.NULL_ENTITY, "Category"));

        if (categorie == BeverageCategory.ALL) {
            return getAllProducts();
        }

        return getAllProducts().stream()
                .filter(p -> p.getCategorie() == categorie)
                .collect(Collectors.toList());
    }

    /**
     * Filter products by type with null-safe handling
     *
     * @param tip Type filter
     * @return List of filtered products
     */
    public List<Product> filterByTip(BeverageType tip) {
        NullSafe.requireNonNull(tip, String.format(ErrorConstants.NULL_ENTITY, "Type"));

        if (tip == BeverageType.ALL) {
            return getAllProducts();
        }

        return getAllProducts().stream()
                .filter(p -> p.getTip() == tip)
                .collect(Collectors.toList());
    }
}