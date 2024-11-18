package OMS.orderActions;

import OMS.enums.OrderStatus;

public class UpdateOrderStatus {
    public final String orderId;
    public final OrderStatus status;

    public UpdateOrderStatus(String orderId, OrderStatus status) {
        this.orderId = orderId;
        this.status = status;
    }
}
