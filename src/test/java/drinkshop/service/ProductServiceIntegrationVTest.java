package drinkshop.service;

import drinkshop.domain.BeverageCategory;
import drinkshop.domain.BeverageType;
import drinkshop.domain.Product;
import drinkshop.repository.Repository;
import drinkshop.service.exception.BusinessException;
import drinkshop.service.validator.ProductValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Step 2: Integrare S + V
 * Repository Mock
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceIntegrationVTest {
    @Mock
    private Repository<Integer, Product> productRepo;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        productService = new ProductService(productRepo, new ProductValidator());
    }

    @Test
    void testAddProduct_Integration_Valid() {
        Product p = new Product(1, "Espresso", 10.0, BeverageCategory.CLASSIC_COFFEE, BeverageType.BASIC);

        // act and assert
        assertDoesNotThrow(() -> productService.addProduct(p));

        // verify
        verify(productRepo).save(p);
    }

    @Test
    void testAddProduct_Integration_Invalid_ThrowsValidationException() {
        Product p = new Product(10, "A", -5.0, BeverageCategory.JUICE, BeverageType.DAIRY);

        // act and assert
        BusinessException exception = assertThrows(BusinessException.class, () -> productService.addProduct(p));

        // verify - repo nu a fost apelat
        verify(productRepo, never()).save(any());
    }
}
