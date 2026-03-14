package drinkshop.domain;

public class Stock {

    private final int id;
    private String ingredient;
    private double cantitate;
    private double stocMinim;

    public Stock(int id, String ingredient, int cantitate, int stocMinim) {
        this.id = id;
        this.ingredient = ingredient;
        this.cantitate = cantitate;
        this.stocMinim = stocMinim;
    }

    // --- getters ---
    public int getId() {
        return id;
    }

    public String getIngredient() {
        return ingredient;
    }

    // --- setters ---
    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public double getCantitate() {
        return cantitate;
    }

    public void setCantitate(double cantitate) {
        this.cantitate = cantitate;
    }

    public double getStocMinim() {
        return stocMinim;
    }

    public void setStocMinim(int stocMinim) {
        this.stocMinim = stocMinim;
    }

    // --- helper methods (safe to keep in entity) ---
    public boolean isSubMinim() {
        return cantitate < stocMinim;
    }

    @Override
    public String toString() {
        return ingredient + " (" + cantitate + " / minim: " + stocMinim + ")";
    }
}