package drinkshop.service.validator;

import drinkshop.domain.OrderItem;
import drinkshop.service.exception.BusinessException;
import drinkshop.service.exception.ErrorConstants;
import drinkshop.service.util.NullSafe;

public class OrderItemValidator implements Validator<OrderItem> {

    @Override
    public void validate(OrderItem item) {
        NullSafe.requireNonNull(item, String.format(ErrorConstants.NULL_ENTITY, "OrderItem"));
        NullSafe.requireNonNull(item.getProduct(), ErrorConstants.ORDER_ITEM_PRODUCT_NOT_FOUND);

        String errors = "";

        if (item.getProduct().getId() <= 0)
            errors += ErrorConstants.INVALID_ID + "\n";

        if (item.getQuantity() <= 0)
            errors += ErrorConstants.ORDER_ITEM_INVALID_QUANTITY + "\n";

        if (!errors.isEmpty())
            throw new BusinessException("ORDER_ITEM_VALIDATION_ERROR", errors.trim());
    }
}
