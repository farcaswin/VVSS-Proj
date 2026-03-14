package drinkshop.service.validator;

import drinkshop.domain.Order;
import drinkshop.domain.OrderItem;
import drinkshop.service.exception.BusinessException;
import drinkshop.service.exception.ErrorConstants;
import drinkshop.service.util.NullSafe;

public class OrderValidator implements Validator<Order> {

    private final OrderItemValidator itemValidator = new OrderItemValidator();

    @Override
    public void validate(Order order) {
        NullSafe.requireNonNull(order, String.format(ErrorConstants.NULL_ENTITY, "Order"));

        String errors = "";

        if (order.getId() <= 0)
            errors += ErrorConstants.INVALID_ID + "\n";

        if (NullSafe.isNullOrEmpty(order.getItems()))
            errors += ErrorConstants.ORDER_EMPTY_ITEMS + "\n";

        if (NullSafe.isNotEmpty(order.getItems())) {
            for (OrderItem item : order.getItems()) {
                try {
                    itemValidator.validate(item);
                } catch (BusinessException e) {
                    errors += e.getMessage() + "\n";
                }
            }
        }

        if (order.getTotalPrice() < 0)
            errors += "Invalid total price\n";

        if (!errors.isEmpty())
            throw new BusinessException("ORDER_VALIDATION_ERROR", errors.trim());
    }
}
