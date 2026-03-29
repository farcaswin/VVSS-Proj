package drinkshop.service.validator;

import drinkshop.domain.Product;
import drinkshop.service.exception.BusinessException;
import drinkshop.service.exception.ErrorConstants;
import drinkshop.service.util.NullSafe;

public class ProductValidator implements Validator<Product> {

    @Override
    public void validate(Product product) {
        NullSafe.requireNonNull(product, String.format(ErrorConstants.NULL_ENTITY, "Product"));

        String errors = "";

        if (product.getId() <= 0)
            errors += ErrorConstants.INVALID_ID + "\n";

        if (NullSafe.isNullOrEmpty(product.getNume()))
            errors += ErrorConstants.INVALID_NAME + "\n";

        if (product.getNume().length() < 3 || product.getNume().length() > 255)
            errors += ErrorConstants.PRODUCT_INVALID_NAME_LENGTH + "\n";

        if (product.getPret() <= 0)
            errors += ErrorConstants.INVALID_PRICE + "\n";

        if (!errors.isEmpty())
            throw new BusinessException("PRODUCT_VALIDATION_ERROR", errors.trim());
    }
}
