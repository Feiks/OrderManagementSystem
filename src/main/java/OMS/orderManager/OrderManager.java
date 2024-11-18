package OMS.orderManager;

import OMS.orderActions.CreateOrder;
import OMS.orderActions.DeleteOrder;
import OMS.orderActions.ListOrders;
import OMS.orderActions.UpdateOrderStatus;
import OMS.orderActor.OrderActor;
import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

import java.util.HashMap;
import java.util.Map;

public class OrderManager extends AbstractActor {
    private final Map<String, ActorRef> orders = new HashMap<>();

    public static Props props() {
        return Props.create(OrderManager.class, OrderManager::new);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(CreateOrder.class, this::onCreateOrder)
                .match(UpdateOrderStatus.class, this::onUpdateOrderStatus)
                .match(DeleteOrder.class, this::onDeleteOrder)
                .match(ListOrders.class, this::onListOrders)
                .build();
    }

    private void onCreateOrder(CreateOrder command) {
        if (!orders.containsKey(command.orderId)) {
            ActorRef orderActor = getContext().actorOf(OrderActor.props(command.orderId), "order-" + command.orderId);
            orders.put(command.orderId, orderActor);
            System.out.println("Order " + command.orderId + " created.");
        } else {
            System.out.println("Order " + command.orderId + " already exists.");
        }
    }

    private void onUpdateOrderStatus(UpdateOrderStatus command) {
        ActorRef orderActor = orders.get(command.orderId);
        if (orderActor != null) {
            orderActor.forward(command, getContext());
        } else {
            System.out.println("Order " + command.orderId + " does not exist.");
        }
    }

    private void onDeleteOrder(DeleteOrder command) {
        ActorRef orderActor = orders.get(command.orderId);
        if (orderActor != null) {
            orderActor.forward(command, getContext());
            orders.remove(command.orderId);
        } else {
            System.out.println("Order " + command.orderId + " does not exist.");
        }
    }

    private void onListOrders(ListOrders command) {
        if (orders.isEmpty()) {
            System.out.println("No orders available.");
        } else {
            System.out.println("Listing all orders:");
            orders.forEach((id, order) -> {
                System.out.println("Order ID: " + id + ", Status: ");
            });
        }
    }
}
