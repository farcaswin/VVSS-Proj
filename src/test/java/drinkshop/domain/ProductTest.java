package drinkshop.domain;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProductTest {

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product(100, "Limonada", 10.0, BeverageCategory.JUICE, BeverageType.WATER_BASED);
    }

    @AfterEach
    void tearDown() {
        product = null;
    }

    @Test
    void testGetId() {
        assert 100 == product.getId();
    }

    @Test
    void testGetNume() {
        assertEquals("Limonada", product.getNume(), "Product name should be \"Limonada\"");
    }

    @Test
    void testSetNume() {
        product.setNume("newLimonada");
        assert "newLimonada".equals(product.getNume());
    }

    @Test
    void testGetPret() {
        assert 10.0 == product.getPret();
    }

    @Test
    void testSetPret() {
        product.setPret(10.05);
        assert 10.05 == product.getPret();
    }

    @Test
    void testGetCategorie() {
        assert BeverageCategory.JUICE.equals(product.getCategorie());
    }

    @Test
    void testSetCategorie() {
        product.setCategorie(BeverageCategory.SMOOTHIE);
        assert BeverageCategory.SMOOTHIE.equals(product.getCategorie());
    }

    @Test
    void testGetTip() {
        assert BeverageType.WATER_BASED.equals(product.getTip());
    }

    @Test
    void testSetTip() {
        product.setTip(BeverageType.BASIC);
        assert BeverageType.BASIC.equals(product.getTip());
    }

    @Test
    void testToString() {
        System.out.println(product.toString());
        assert "Limonada (JUICE, WATER_BASED) - 10.0 lei".equals(product.toString());
    }
}