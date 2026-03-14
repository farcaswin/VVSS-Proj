package drinkshop.service.validator;

import drinkshop.domain.Stoc;
import drinkshop.service.exception.BusinessException;
import drinkshop.service.exception.ErrorConstants;
import drinkshop.service.util.NullSafe;

public class StocValidator implements Validator<Stoc> {

    @Override
    public void validate(Stoc stoc) {
        NullSafe.requireNonNull(stoc, String.format(ErrorConstants.NULL_ENTITY, "Stock"));

        String errors = "";

        if (stoc.getId() <= 0)
            errors += ErrorConstants.INVALID_ID + "\n";

        if (NullSafe.isNullOrEmpty(stoc.getIngredient()))
            errors += "Invalid ingredient: Ingredient name cannot be null or empty\n";

        if (stoc.getCantitate() < 0)
            errors += ErrorConstants.STOCK_INVALID_QUANTITY + "\n";

        if (stoc.getStocMinim() < 0)
            errors += "Invalid minimum stock: Minimum stock cannot be negative\n";

        if (stoc.getCantitate() < stoc.getStocMinim())
            errors += "Quantity is below minimum stock level\n";

        if (!errors.isEmpty())
            throw new BusinessException("STOCK_VALIDATION_ERROR", errors.trim());
    }
}