package OMS.orderActor;

import OMS.enums.OrderStatus;
import OMS.orderActions.DeleteOrder;
import OMS.orderActions.ListOrders;
import OMS.orderActions.UpdateOrderStatus;
import akka.actor.AbstractActor;

import akka.actor.AbstractActor;
import akka.actor.Props;

public class OrderActor extends AbstractActor {
    private final String orderId;
    private OrderStatus status;

    public OrderActor(String orderId) {
        this.orderId = orderId;
        this.status = OrderStatus.CREATED;
    }

    public static Props props(String orderId) {
        return Props.create(OrderActor.class, () -> new OrderActor(orderId));
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(UpdateOrderStatus.class, this::onUpdateOrderStatus)
                .match(DeleteOrder.class, this::onDeleteOrder)
                .match(ListOrders.class, this::onListOrder)
                .build();
    }

    private void onUpdateOrderStatus(UpdateOrderStatus command) {
        if (command.status != null) {
            this.status = command.status;
            System.out.println("Order " + orderId + " status updated to " + status);
        } else {
            System.out.println("Invalid status update for order " + orderId);
        }
    }

    private void onDeleteOrder(DeleteOrder command) {
        System.out.println("Order " + orderId + " deleted");
        getContext().stop(getSelf());
    }

    private void onListOrder(ListOrders command) {
        getSender().tell(new OrderDetails(orderId, status), getSelf());
    }

    public static class OrderDetails {
        public final String orderId;
        public final OrderStatus status;

        public OrderDetails(String orderId, OrderStatus status) {
            this.orderId = orderId;
            this.status = status;
        }
    }
}

