package drinkshop.service.validator;

import drinkshop.domain.Recipe;
import drinkshop.domain.RecipeIngredient;
import drinkshop.service.exception.BusinessException;
import drinkshop.service.exception.ErrorConstants;
import drinkshop.service.util.NullSafe;

import java.util.List;

public class RecipeValidator implements Validator<Recipe> {

    @Override
    public void validate(Recipe recipe) {
        NullSafe.requireNonNull(recipe, String.format(ErrorConstants.NULL_ENTITY, "Recipe"));

        final String[] errors = {""};

        if (recipe.getId() <= 0)
            errors[0] += ErrorConstants.INVALID_ID + "\n";

        List<RecipeIngredient> ingrediente = recipe.getIngrediente();
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
