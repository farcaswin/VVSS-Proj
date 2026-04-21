package drinkshop.service;

import drinkshop.domain.BeverageCategory;
import drinkshop.domain.BeverageType;
import drinkshop.domain.Product;
import drinkshop.repository.AbstractRepository;
import drinkshop.repository.Repository;
import drinkshop.service.validator.ProductValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FilterProductWBTTest {

    private ProductService productService;
    private Repository<Integer, Product> dummyRepo;

    @BeforeEach
    void setUp() {
        dummyRepo = new AbstractRepository<Integer, Product>() {
            @Override
            protected Integer getId(Product entity) {
                return entity != null ? entity.getId() : 0;
            }
        };
        productService = new ProductService(dummyRepo, new ProductValidator());
    }

    @Test
    @DisplayName("Exceptie: Categoria este null (Nu ajunge la nodul 3)")
    void testFilter_NullCategory() {
        assertThrows(IllegalArgumentException.class, () -> {
            productService.filterByCategorie(null);
        });
    }

    @Test
    @DisplayName("Path 1: Categoria este ALL -> Returneaza tot")
    void testFilter_CategoryAll() {
        dummyRepo.save(new Product(1, "Latte", 10.0, BeverageCategory.MILK_COFFEE, BeverageType.DAIRY));

        List<Product> result = productService.filterByCategorie(BeverageCategory.ALL);
        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Path 2: Lista este goala -> Nu intra in for")
    void testFilter_EmptyList() {
        List<Product> result = productService.filterByCategorie(BeverageCategory.MILK_COFFEE);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Path 3: Produsul este null in interiorul listei (Sare peste add)")
    void testFilter_NullProductInList() {
        // Cream un repo special care returneaza o lista ce contine un element null
        Repository<Integer, Product> repoWithNull = new AbstractRepository<Integer, Product>() {
            @Override
            protected Integer getId(Product entity) { return 0; }
            @Override
            public List<Product> findAll() {
                return Arrays.asList((Product) null);
            }
        };
        ProductService serviceWithNullRepo = new ProductService(repoWithNull, new ProductValidator());

        List<Product> result = serviceWithNullRepo.filterByCategorie(BeverageCategory.MILK_COFFEE);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Path 4: Produsul exista, dar nu face parte din categoria cautata")
    void testFilter_DifferentCategory() {
        // Salvam ceai, dar cautam cafea cu lapte
        dummyRepo.save(new Product(1, "Ceai", 10.0, BeverageCategory.TEA, BeverageType.WATER_BASED));

        List<Product> result = productService.filterByCategorie(BeverageCategory.MILK_COFFEE);
        assertEquals(0, result.size());
    }

    @Test
    @DisplayName("Path 5: Produsul este valid, are categoria corecta si este adaugat in lista")
    void testFilter_MatchingCategory() {
        dummyRepo.save(new Product(1, "Latte", 10.0, BeverageCategory.MILK_COFFEE, BeverageType.DAIRY));

        List<Product> result = productService.filterByCategorie(BeverageCategory.MILK_COFFEE);
        assertEquals(1, result.size());
        assertEquals(BeverageCategory.MILK_COFFEE, result.get(0).getCategorie());
    }
}