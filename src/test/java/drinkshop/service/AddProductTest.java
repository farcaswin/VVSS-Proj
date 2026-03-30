package drinkshop.service;

import drinkshop.domain.BeverageCategory;
import drinkshop.domain.BeverageType;
import drinkshop.domain.Product;
import drinkshop.repository.AbstractRepository;
import drinkshop.repository.Repository;
import drinkshop.service.exception.BusinessException;
import drinkshop.service.validator.ProductValidator;
import drinkshop.service.validator.Validator;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.api.Nested;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Tag("BlackBoxTesting")
class AddProductTest {

    private ProductService productService;
    private Repository<Integer, Product> dummyRepo;

    @BeforeEach
    void setUp() {
        dummyRepo = new AbstractRepository<Integer, Product>() {
            @Override
            protected Integer getId(Product entity) { return entity.getId(); }
        };
        Validator<Product> validator = new ProductValidator();
        productService = new ProductService(dummyRepo, validator);
    }

    // ==========================================
    // CAZURI ECP (Equivalence Class Partitioning)
    // ==========================================

    @Nested
    class ECPTestsClass {

        @Test
        @Order(1)
        @DisplayName("TC1_ECP: Adaugare produs cu date valide")
        @Timeout(value = 1, unit = TimeUnit.SECONDS)
        void testAddProduct_ECP_Valid() {
            Product validProduct = new Product(1, "Latte", 12.0, BeverageCategory.MILK_COFFEE, BeverageType.DAIRY);

            // ACT
            productService.addProduct(validProduct);

            // ASSERT
            assertNotNull(dummyRepo.findOne(1));    // means product was added
        }

        @Test
        @Order(2)
        @DisplayName("TC4_ECP: Nume prea scurt (Invalid EC)")
        void testAddProduct_ECP_NameTooShort() {
            // ARRANGE
            Product invalidProduct = new Product(1, "Ca", 12.0, BeverageCategory.MILK_COFFEE, BeverageType.DAIRY);

            // ACT
            BusinessException exception = assertThrows(BusinessException.class, () -> {
                productService.addProduct(invalidProduct);
            });

            // ASSERT
            assertEquals("PRODUCT_VALIDATION_ERROR", exception.getErrorCode());
            assertEquals("Invalid product name length: Name must have at least 3 letters and less than 255", exception.getMessage());
        }

        @Test
        @Order(3)
        @DisplayName("TC9_ECP: Pret < 0 (Invalid EC)")
        void testAddProduct_ECP_PriceNegative() {
            // ARRANGE
            Product invalidProduct = new Product(1, "Latte", -12.0, BeverageCategory.MILK_COFFEE, BeverageType.DAIRY);

            // ACT
            BusinessException exception = assertThrows(BusinessException.class, () -> {
                productService.addProduct(invalidProduct);
            });

            // ASSERT
            assertEquals("PRODUCT_VALIDATION_ERROR", exception.getErrorCode());
            assertEquals("Invalid price: Price must be greater than 0", exception.getMessage());

        }

        // Parameterized tests for invalid ECP cases with csv values
        @ParameterizedTest
        @CsvSource({
                "Ca, 12.0, PRODUCT_VALIDATION_ERROR, Invalid product name length: Name must have at least 3 letters and less than 255",
                "Latte, -12.0, PRODUCT_VALIDATION_ERROR, Invalid price: Price must be greater than 0"
        })
        void testAddProduct_ECP_Parameterized(String name, double price, String expectedErrorCode, String expectedMessage) {
            // ARRANGE
            Product invalidProduct = new Product(1, name, price, BeverageCategory.MILK_COFFEE, BeverageType.DAIRY);

            // ACT
            BusinessException exception = assertThrows(BusinessException.class, () -> {
                productService.addProduct(invalidProduct);
            });

            // ASSERT
            assertEquals(expectedErrorCode, exception.getErrorCode());
            assertEquals(expectedMessage, exception.getMessage());
        }
    }

    // ==========================================
    // CAZURI BVA (Boundary Value Analysis)
    // ==========================================

    @Nested
    class BVATestesClass {

        @Test
        @Order(4)
        @DisplayName("TC2_BVA: Nume valid limta inferioara (length = 3)")
        void testAddProduct_BVA_NameLength3() {
            // ARRANGE
            Product validProduct = new Product(1, "Tea", 12.0, BeverageCategory.MILK_COFFEE, BeverageType.DAIRY);

            // ACT
            productService.addProduct(validProduct);

            // ASSERT
            assertNotNull(dummyRepo.findOne(1));
        }

        @Test
        @Order(5)
        @DisplayName("TC4_BVA: Nume valid limita superioara (length = 255)")
        void testAddProduct_BVA_NameLength255() {
            // ARRANGE
            String name255 = "M".repeat(255); // Genereaza un string de 255 caractere
            Product validProduct = new Product(1, name255, 12.0, BeverageCategory.MILK_COFFEE, BeverageType.DAIRY);

            // ACT
            productService.addProduct(validProduct);

            // ASSERT
            assertNotNull(dummyRepo.findOne(1));
        }

        @Test
        @Order(6)
        @DisplayName("TC6_BVA: Nume invalid limita superioara (length = 256)")
        void testAddProduct_BVA_NameLength256() {
            // ARRANGE
            String name256 = "M".repeat(256);
            Product invalidProduct = new Product(1, name256, 12.0, BeverageCategory.MILK_COFFEE, BeverageType.DAIRY);

            // ACT
            BusinessException exception = assertThrows(BusinessException.class, () -> {
                productService.addProduct(invalidProduct);
            });

            // ASSERT
            assertEquals("PRODUCT_VALIDATION_ERROR", exception.getErrorCode());
            assertEquals("Invalid product name length: Name must have at least 3 letters and less than 255", exception.getMessage());

        }

        @Test
        @Order(7)
        @DisplayName("TC8_BVA: Pret invalid limita inferioara (pret = 0.0)")
        void testAddProduct_BVA_PriceZero() {
            // ARRANGE
            Product invalidProduct = new Product(1, "Late", 0.0, BeverageCategory.MILK_COFFEE, BeverageType.DAIRY);

            // ACT
            BusinessException exception = assertThrows(BusinessException.class, () -> {
                productService.addProduct(invalidProduct);
            });

            // ASSERT
            assertEquals("PRODUCT_VALIDATION_ERROR", exception.getErrorCode());
            assertEquals("Invalid price: Price must be greater than 0", exception.getMessage());

        }

        @Test
        @Order(8)
        @DisplayName("TC9_BVA: Pret valid (pret = 1.0)")
        void testAddProduct_BVA_PriceOne() {
            // ARRANGE
            Product validProduct = new Product(1, "Late", 1.0, BeverageCategory.MILK_COFFEE, BeverageType.DAIRY);

            // ACT
            productService.addProduct(validProduct);

            // ASSERT
            assertNotNull(dummyRepo.findOne(1));
        }
    }
}