package drinkshop.service;

import drinkshop.domain.BeverageCategory;
import drinkshop.domain.BeverageType;
import drinkshop.domain.Product;
import drinkshop.repository.Repository;
import drinkshop.service.exception.BusinessException;
import drinkshop.service.validator.Validator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceUnitTest {
    @Mock
    private Repository<Integer, Product> productRepo;
    @Mock
    private Validator<Product> productValidator;
    @InjectMocks
    private ProductService productService;

    @Test
    void testAddProduct_Success() {
        // arrange
        Product p = new Product(1, "Espresso", 10.0, BeverageCategory.CLASSIC_COFFEE, BeverageType.BASIC);
    
        // act and assert
        assertDoesNotThrow(() -> productService.addProduct(p));

        // verify
        verify(productValidator).validate(p);
        verify(productRepo).save(p);
    }

    @Test
    void testDeleteProduct_NotFound_ThrowsException() {
        // arrange
        int id = 999;
        // stub
        when(productRepo.findOne(id)).thenReturn(null);

        // act and assert
        BusinessException exception = assertThrows(BusinessException.class, () -> productService.deleteProduct(id));
        assertEquals("PRODUCT_NOT_FOUND", exception.getErrorCode());

        // verify
        verify(productRepo).findOne(id);
        verify(productRepo, never()).delete(id);
    }
}
