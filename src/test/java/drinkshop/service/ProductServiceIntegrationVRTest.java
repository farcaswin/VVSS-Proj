package drinkshop.service;

import drinkshop.domain.BeverageCategory;
import drinkshop.domain.BeverageType;
import drinkshop.domain.Product;
import drinkshop.repository.AbstractRepository;
import drinkshop.repository.Repository;
import drinkshop.service.validator.ProductValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Step 3: Integrare S + V + R
 * Componente reale, fara mock-uri
 */
class ProductServiceIntegrationVRTest {
    private ProductService productService;
    private Repository<Integer, Product> productRepo;

    @BeforeEach
    void setUp() {
        productRepo = new AbstractRepository<Integer, Product>() {
            @Override
            protected Integer getId(Product entity) {
                return entity.getId();
            }
        };
        productService = new ProductService(productRepo, new ProductValidator());
    }

    @Test
    void testAddAndFindProduct_FullIntegration() {
        Product p = new Product(50, "Smoothie", 20.0, BeverageCategory.SMOOTHIE, BeverageType.PLANT_BASED);

        // act
        productService.addProduct(p);
        Product found = productRepo.findOne(50);

        // assert
        assertNotNull(found);
        assertEquals("Smoothie", found.getNume());
        assertEquals(20.0, found.getPret());
    }

    @Test
    void testDeleteProduct_FullIntegration() {
        // arrange
        Product p = new Product(60, "Cold Brew", 18.0, BeverageCategory.ICED_COFFEE, BeverageType.BASIC);
        productRepo.save(p);

        // act
        productService.deleteProduct(60);

        // assert
        assertNull(productRepo.findOne(60));
    }
}
