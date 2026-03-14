package drinkshop.service;

import drinkshop.domain.*;
import drinkshop.export.CsvExporter;
import drinkshop.receipt.ReceiptGenerator;
import drinkshop.reports.DailyReportService;
import drinkshop.repository.Repository;
import drinkshop.service.exception.BusinessException;
import drinkshop.service.util.NullSafe;
import drinkshop.service.validator.*;

import java.util.List;
import java.util.Optional;

/**
 * DrinkShopService - Main orchestrator service for the entire application.
 * Manages all services and their dependency injection with validators.
 * Implements centralized error handling through BusinessException.
 */
public class DrinkShopService {

    private final ProductService productService;
    private final OrderService orderService;
    private final RetetaService retetaService;
    private final StocService stocService;
    private final DailyReportService report;

    /**
     * Constructor with full Dependency Injection
     * Initializes all services with their respective validators
     */
    public DrinkShopService(
            Repository<Integer, Product> productRepo,
            Repository<Integer, Order> orderRepo,
            Repository<Integer, Reteta> retetaRepo,
            Repository<Integer, Stoc> stocRepo
    ) {
        NullSafe.requireNonNull(productRepo, "Product repository cannot be null");
        NullSafe.requireNonNull(orderRepo, "Order repository cannot be null");
        NullSafe.requireNonNull(retetaRepo, "Recipe repository cannot be null");
        NullSafe.requireNonNull(stocRepo, "Stock repository cannot be null");
        
        // Initialize validators (C06 - Dependency Injection of Validators)
        Validator<Product> productValidator = new ProductValidator();
        Validator<Order> orderValidator = new OrderValidator();
        Validator<Reteta> retetaValidator = new RetetaValidator();
        Validator<Stoc> stocValidator = new StocValidator();
        
        // Initialize services with validators injected
        this.productService = new ProductService(productRepo, productValidator);
        this.orderService = new OrderService(orderRepo, productRepo, orderValidator);
        this.retetaService = new RetetaService(retetaRepo, retetaValidator);
        this.stocService = new StocService(stocRepo, stocValidator);
        this.report = new DailyReportService(orderRepo);
    }

    // ==================== PRODUCT SERVICE ====================
    public void addProduct(Product p) {
        productService.addProduct(p);
    }

    public void updateProduct(int id, String name, double price, CategorieBautura categorie, TipBautura tip) {
        productService.updateProduct(id, name, price, categorie, tip);
    }

    public void deleteProduct(int id) {
        productService.deleteProduct(id);
    }

    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    public Optional<Product> getProductById(int id) {
        return productService.findById(id);
    }

    public List<Product> filtreazaDupaCategorie(CategorieBautura categorie) {
        return productService.filterByCategorie(categorie);
    }

    public List<Product> filtreazaDupaTip(TipBautura tip) {
        return productService.filterByTip(tip);
    }

    // ---------- ORDER ----------
    public void addOrder(Order o) {
        orderService.addOrder(o);
    }

    public List<Order> getAllOrders() {
        return orderService.getAllOrders();
    }

    public Optional<Order> getOrderById(int id) {
        return orderService.findById(id);
    }

    public void deleteOrder(int id) {
        orderService.deleteOrder(id);
    }

    public double computeTotal(Order o) {
        return orderService.computeTotal(o);
    }

    public String generateReceipt(Order o) {
        NullSafe.requireNonNull(o, "Order cannot be null");
        return ReceiptGenerator.generate(o, productService.getAllProducts());
    }

    public double getDailyRevenue() {
        return report.getTotalRevenue();
    }

    public void exportCsv(String path) {
        NullSafe.requireNonNull(path, "Export path cannot be null");
        CsvExporter.exportOrders(productService.getAllProducts(), orderService.getAllOrders(), path);
    }

    // ==================== STOCK + RECIPE SERVICE ====================
    
    /**
     * Order product - checks if sufficient stock and consumes ingredients
     * (C05 - Null checks, C06 - Validation, C08 - Custom errors)
     */
    public void comandaProdus(Product produs) {
        NullSafe.requireNonNull(produs, "Product cannot be null");
        
        try {
            // C05: Null-safe recipe lookup using Optional
            Optional<Reteta> recipeOpt = retetaService.findById(produs.getId());
            
            if (recipeOpt.isEmpty()) {
                throw new BusinessException("RECIPE_NOT_FOUND",
                    String.format("No recipe found for product: %s", produs.getNume()));
            }
            
            Reteta reteta = recipeOpt.get();
            
            // C06: Validate sufficient stock before consuming
            if (!stocService.areSuficient(reteta)) {
                throw new BusinessException("INSUFFICIENT_STOCK",
                    String.format("Insufficient stock to produce: %s", produs.getNume()));
            }
            
            stocService.consuma(reteta);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("PRODUCT_ORDER_FAILED",
                String.format("Failed to order product %s: %s", produs.getNume(), e.getMessage()), e);
        }
    }

    /**
     * Get all recipes
     */
    public List<Reteta> getAllRetete() {
        return retetaService.getAll();
    }

    /**
     * Add recipe with validation
     */
    public void addReteta(Reteta r) {
        retetaService.addReteta(r);
    }

    /**
     * Update recipe with validation
     */
    public void updateReteta(Reteta r) {
        retetaService.updateReteta(r);
    }

    /**
     * Delete recipe with existence check
     */
    public void deleteReteta(int id) {
        retetaService.deleteReteta(id);
    }

    /**
     * Find recipe by ID using Optional pattern
     */
    public Optional<Reteta> getRetetaById(int id) {
        return retetaService.findById(id);
    }

    // ==================== STOCK SERVICE ====================
    public void addStoc(Stoc s) {
        stocService.add(s);
    }

    public void updateStoc(Stoc s) {
        stocService.update(s);
    }

    public void deleteStoc(int id) {
        stocService.delete(id);
    }

    public List<Stoc> getAllStoc() {
        return stocService.getAll();
    }

    public boolean checkStockSufficiency(Reteta reteta) {
        return stocService.areSuficient(reteta);
    }
}