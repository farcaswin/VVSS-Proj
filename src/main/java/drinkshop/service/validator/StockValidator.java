package drinkshop.service.validator;

import drinkshop.domain.Stock;
import drinkshop.service.exception.BusinessException;
import drinkshop.service.exception.ErrorConstants;
import drinkshop.service.util.NullSafe;

public class StockValidator implements Validator<Stock> {

    @Override
    public void validate(Stock stock) {
        NullSafe.requireNonNull(stock, String.format(ErrorConstants.NULL_ENTITY, "Stock"));

        String errors = "";

        if (stock.getId() <= 0)
            errors += ErrorConstants.INVALID_ID + "\n";

        if (NullSafe.isNullOrEmpty(stock.getIngredient()))
            errors += "Invalid ingredient: Ingredient name cannot be null or empty\n";

        if (stock.getCantitate() < 0)
            errors += ErrorConstants.STOCK_INVALID_QUANTITY + "\n";

        if (stock.getStocMinim() < 0)
            errors += "Invalid minimum stock: Minimum stock cannot be negative\n";

        if (stock.getCantitate() < stock.getStocMinim())
            errors += "Quantity is below minimum stock level\n";

        if (!errors.isEmpty())
            throw new BusinessException("STOCK_VALIDATION_ERROR", errors.trim());
    }
}