package drinkshop.service;

import drinkshop.domain.Reteta;
import drinkshop.repository.Repository;
import drinkshop.service.exception.BusinessException;
import drinkshop.service.exception.ErrorConstants;
import drinkshop.service.util.NullSafe;
import drinkshop.service.validator.RetetaValidator;
import drinkshop.service.validator.Validator;

import java.util.List;
import java.util.Optional;

/**
 * RetetaService - Manages all recipe-related business operations.
 * Includes dependency injection for RetetaValidator (C06 - Input Validation).
 * Includes null-safe checks and Optional pattern (C05 - NullPointerException Prevention).
 * Includes custom error messages (C08 - Error Handling).
 */
public class RetetaService {

    private final Repository<Integer, Reteta> retetaRepo;
    private final Validator<Reteta> retetaValidator;

    /**
     * Constructor with Dependency Injection
     * @param retetaRepo Repository for recipe persistence
     * @param retetaValidator Validator for recipe validation
     */
    public RetetaService(Repository<Integer, Reteta> retetaRepo, Validator<Reteta> retetaValidator) {
        this.retetaRepo = NullSafe.requireNonNull(retetaRepo, ErrorConstants.NULL_REPOSITORY);
        this.retetaValidator = NullSafe.requireNonNull(retetaValidator, ErrorConstants.NULL_VALIDATOR);
    }

    /**
     * Add new recipe with validation (C06 - Input Data Validation)
     * @param r Recipe to add
     * @throws BusinessException if recipe validation fails
     */
    public void addReteta(Reteta r) {
        // C06: Validate input data before processing
        retetaValidator.validate(r);
        
        NullSafe.requireNonNull(r, String.format(ErrorConstants.NULL_ENTITY, "Recipe"));
        
        try {
            retetaRepo.save(r);
        } catch (Exception e) {
            throw new BusinessException("RECIPE_ADD_FAILED", 
                String.format(ErrorConstants.OPERATION_FAILED, e.getMessage()), e);
        }
    }

    /**
     * Update existing recipe with validation (C06 - Input Data Validation)
     * @param r Recipe to update
     * @throws BusinessException if validation fails or recipe not found
     */
    public void updateReteta(Reteta r) {
        // C06: Validate before update
        retetaValidator.validate(r);
        
        NullSafe.requireNonNull(r, String.format(ErrorConstants.NULL_ENTITY, "Recipe"));
        
        // C05: Check if recipe exists
        Optional<Reteta> existing = Optional.ofNullable(retetaRepo.findOne(r.getId()));
        if (existing.isEmpty()) {
            throw new BusinessException("RECIPE_NOT_FOUND", 
                String.format(ErrorConstants.RECIPE_NOT_FOUND, r.getId()));
        }
        
        try {
            retetaRepo.update(r);
        } catch (Exception e) {
            throw new BusinessException("RECIPE_UPDATE_FAILED", 
                String.format(ErrorConstants.OPERATION_FAILED, e.getMessage()), e);
        }
    }

    /**
     * Delete recipe with existence check (C05 - NullPointerException Prevention)
     * @param id Recipe ID
     * @throws BusinessException if recipe not found
     */
    public void deleteReteta(int id) {
        // C05: Check if recipe exists before deletion
        Optional<Reteta> reteta = Optional.ofNullable(retetaRepo.findOne(id));
        
        if (reteta.isEmpty()) {
            throw new BusinessException("RECIPE_NOT_FOUND", 
                String.format(ErrorConstants.RECIPE_NOT_FOUND, id));
        }
        
        try {
            retetaRepo.delete(id);
        } catch (Exception e) {
            throw new BusinessException("RECIPE_DELETE_FAILED", 
                String.format(ErrorConstants.OPERATION_FAILED, e.getMessage()), e);
        }
    }

    /**
     * Find recipe by ID with null-safe handling (C05 - Optional Pattern)
     * @param id Recipe ID
     * @return Optional containing recipe if found
     */
    public Optional<Reteta> findById(int id) {
        try {
            return Optional.ofNullable(retetaRepo.findOne(id));
        } catch (Exception e) {
            throw new BusinessException("RECIPE_FIND_FAILED", 
                String.format(ErrorConstants.OPERATION_FAILED, e.getMessage()), e);
        }
    }

    /**
     * Get all recipes (C05 - Null-safe handling)
     * @return List of all recipes
     */
    public List<Reteta> getAll() {
        try {
            List<Reteta> recipes = retetaRepo.findAll();
            // C05: Null-safe check
            return NullSafe.isNotEmpty(recipes) ? recipes : List.of();
        } catch (Exception e) {
            throw new BusinessException("RECIPES_FETCH_FAILED", 
                String.format(ErrorConstants.OPERATION_FAILED, e.getMessage()), e);
        }
    }
}