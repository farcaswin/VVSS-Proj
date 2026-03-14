package drinkshop.service;

import drinkshop.domain.Recipe;
import drinkshop.domain.RecipeIngredient;
import drinkshop.domain.Stock;
import drinkshop.repository.Repository;
import drinkshop.service.exception.BusinessException;
import drinkshop.service.exception.ErrorConstants;
import drinkshop.service.util.NullSafe;
import drinkshop.service.validator.Validator;

import java.util.List;
import java.util.Optional;

/**
 * StocService - Manages all stock-related business operations.
 * Includes dependency injection for StocValidator (C06 - Input Validation).
 * Includes null-safe checks and Optional pattern (C05 - NullPointerException Prevention).
 * Includes custom error messages (C08 - Error Handling).
 */
public class StockService {

    private final Repository<Integer, Stock> stocRepo;
    private final Validator<Stock> stocValidator;

    /**
     * Constructor with Dependency Injection
     *
     * @param stocRepo      Repository for stock persistence
     * @param stocValidator Validator for stock validation
     */
    public StockService(Repository<Integer, Stock> stocRepo, Validator<Stock> stocValidator) {
        this.stocRepo = NullSafe.requireNonNull(stocRepo, ErrorConstants.NULL_REPOSITORY);
        this.stocValidator = NullSafe.requireNonNull(stocValidator, ErrorConstants.NULL_VALIDATOR);
    }

    /**
     * Get all stocks (C05 - Null-safe handling)
     *
     * @return List of all stocks
     */
    public List<Stock> getAll() {
        try {
            List<Stock> stocks = stocRepo.findAll();
            // C05: Null-safe check
            return NullSafe.isNotEmpty(stocks) ? stocks : List.of();
        } catch (Exception e) {
            throw new BusinessException("STOCKS_FETCH_FAILED",
                    String.format(ErrorConstants.OPERATION_FAILED, e.getMessage()), e);
        }
    }

    /**
     * Add new stock with validation (C06 - Input Data Validation)
     *
     * @param s Stock to add
     * @throws BusinessException if stock validation fails
     */
    public void add(Stock s) {
        // C06: Validate input data before processing
        stocValidator.validate(s);

        NullSafe.requireNonNull(s, String.format(ErrorConstants.NULL_ENTITY, "Stock"));

        try {
            stocRepo.save(s);
        } catch (Exception e) {
            throw new BusinessException("STOCK_ADD_FAILED",
                    String.format(ErrorConstants.OPERATION_FAILED, e.getMessage()), e);
        }
    }

    /**
     * Update existing stock with validation (C06 - Input Data Validation)
     *
     * @param s Stock to update
     * @throws BusinessException if validation fails or stock not found
     */
    public void update(Stock s) {
        // C06: Validate before update
        stocValidator.validate(s);

        NullSafe.requireNonNull(s, String.format(ErrorConstants.NULL_ENTITY, "Stock"));

        // C05: Check if stock exists
        Optional<Stock> existing = Optional.ofNullable(stocRepo.findOne(s.getId()));
        if (existing.isEmpty()) {
            throw new BusinessException("STOCK_NOT_FOUND",
                    String.format(ErrorConstants.STOCK_NOT_FOUND, s.getId()));
        }

        try {
            stocRepo.update(s);
        } catch (Exception e) {
            throw new BusinessException("STOCK_UPDATE_FAILED",
                    String.format(ErrorConstants.OPERATION_FAILED, e.getMessage()), e);
        }
    }

    /**
     * Delete stock with existence check (C05 - NullPointerException Prevention)
     *
     * @param id Stock ID
     * @throws BusinessException if stock not found
     */
    public void delete(int id) {
        // C05: Check if stock exists before deletion
        Optional<Stock> stock = Optional.ofNullable(stocRepo.findOne(id));

        if (stock.isEmpty()) {
            throw new BusinessException("STOCK_NOT_FOUND",
                    String.format(ErrorConstants.STOCK_NOT_FOUND, id));
        }

        try {
            stocRepo.delete(id);
        } catch (Exception e) {
            throw new BusinessException("STOCK_DELETE_FAILED",
                    String.format(ErrorConstants.OPERATION_FAILED, e.getMessage()), e);
        }
    }

    /**
     * Check if sufficient stock available for recipe (C05 - Null-safe checks, C08 - Custom errors)
     *
     * @param recipe Recipe to check stock for
     * @return true if sufficient stock, false otherwise
     * @throws BusinessException if recipe is null or stock check fails
     */
    public boolean areSuficient(Recipe recipe) {
        NullSafe.requireNonNull(recipe, String.format(ErrorConstants.NULL_ENTITY, "Recipe"));
        NullSafe.requireNonNull(recipe.getIngrediente(), String.format(ErrorConstants.NULL_ENTITY, "Recipe ingredients"));

        try {
            List<RecipeIngredient> ingredienteNecesare = recipe.getIngrediente();

            for (RecipeIngredient e : ingredienteNecesare) {
                // C05: Null-safe ingredient checks
                if (NullSafe.isNullOrEmpty(e.getDenumire())) {
                    throw new BusinessException("INVALID_INGREDIENT",
                            "Recipe contains invalid ingredient name");
                }

                String ingredient = e.getDenumire();
                double necesar = e.getCantitate();

                double disponibil = getAll().stream()
                        .filter(s -> s.getIngredient().equalsIgnoreCase(ingredient))
                        .mapToDouble(Stock::getCantitate)
                        .sum();

                if (disponibil < necesar) {
                    // C08: Custom error message with specific details
                    throw new BusinessException("INSUFFICIENT_STOCK",
                            String.format("Insufficient stock for ingredient '%s': required %.2f, available %.2f",
                                    ingredient, necesar, disponibil));
                }
            }
            return true;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("STOCK_CHECK_FAILED",
                    String.format(ErrorConstants.OPERATION_FAILED, e.getMessage()), e);
        }
    }

    /**
     * Consume ingredients from stock for recipe (C05 - Null-safe, C06 - Validate before consume, C08 - Error messages)
     *
     * @param recipe Recipe to consume ingredients for
     * @throws BusinessException if insufficient stock or operation fails
     */
    public void consuma(Recipe recipe) {
        NullSafe.requireNonNull(recipe, String.format(ErrorConstants.NULL_ENTITY, "Recipe"));

        try {
            // C06: Validate sufficient stock before consuming
            if (!areSuficient(recipe)) {
                throw new BusinessException("INSUFFICIENT_STOCK", ErrorConstants.STOCK_INSUFFICIENT);
            }

            for (RecipeIngredient e : recipe.getIngrediente()) {
                String ingredient = e.getDenumire();
                double necesar = e.getCantitate();

                List<Stock> ingredienteStock = getAll().stream()
                        .filter(s -> s.getIngredient().equalsIgnoreCase(ingredient))
                        .toList();

                double ramas = necesar;

                for (Stock s : ingredienteStock) {
                    if (ramas <= 0) break;

                    double deScazut = Math.min(s.getCantitate(), ramas);
                    s.setCantitate((int) (s.getCantitate() - deScazut));
                    ramas -= deScazut;

                    // C06: Validate before updating consumed stock
                    stocValidator.validate(s);
                    stocRepo.update(s);
                }
            }
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("STOCK_CONSUMPTION_FAILED",
                    String.format(ErrorConstants.OPERATION_FAILED, e.getMessage()), e);
        }
    }
}