package drinkshop.service.validator;

import drinkshop.domain.IngredientReteta;
import drinkshop.domain.Reteta;
import drinkshop.service.exception.BusinessException;
import drinkshop.service.exception.ErrorConstants;
import drinkshop.service.util.NullSafe;

import java.util.List;

public class RetetaValidator implements Validator<Reteta> {

    @Override
    public void validate(Reteta reteta) {
        NullSafe.requireNonNull(reteta, String.format(ErrorConstants.NULL_ENTITY, "Recipe"));

        final String[] errors = {""};

        if (reteta.getId() <= 0)
            errors[0] += ErrorConstants.INVALID_ID + "\n";

        List<IngredientReteta> ingrediente = reteta.getIngrediente();
        if (NullSafe.isNullOrEmpty(ingrediente))
            errors[0] += ErrorConstants.RECIPE_EMPTY_INGREDIENTS + "\n";

        if (NullSafe.isNotEmpty(ingrediente)) {
            ingrediente.stream()
                    .filter(entry -> entry.getCantitate() <= 0)
                    .forEach(entry -> {
                        errors[0] += ErrorConstants.RECIPE_INGREDIENT_INVALID_QUANTITY + " for [" + entry.getDenumire() + "]\n";
                    });
        }

        if (!errors[0].isEmpty())
            throw new BusinessException("RECIPE_VALIDATION_ERROR", errors[0].trim());
    }
}
