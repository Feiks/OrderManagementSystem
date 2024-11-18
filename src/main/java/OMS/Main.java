package OMS;

import OMS.enums.OrderStatus;
import OMS.orderActions.CreateOrder;
import OMS.orderActions.DeleteOrder;
import OMS.orderActions.ListOrders;
import OMS.orderActions.UpdateOrderStatus;
import OMS.orderManager.OrderManager;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        ActorSystem system = ActorSystem.create("order-system");
        ActorRef orderManager = system.actorOf(OrderManager.props(), "orderManager");

        Scanner scanner = new Scanner(System.in);

        System.out.println("Order Management System. Enter a command:");
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            String[] command = input.split(" ");
            switch (command[0]) {
                case "create":
                    if (command.length > 1) {
                        orderManager.tell(new CreateOrder(command[1]), ActorRef.noSender());
                    } else {
                        System.out.println("Usage: create <orderId>");
                    }
                    break;
                case "update":
                    if (command.length > 2) {
                        try {
                            OrderStatus status = OrderStatus.valueOf(command[2].toUpperCase());
                            orderManager.tell(new UpdateOrderStatus(command[1], status), ActorRef.noSender());
                        } catch (IllegalArgumentException e) {
                            System.out.println("Invalid status. Use one of: CREATED, IN_PROGRESS, COMPLETED, CANCELLED.");
                        }
                    } else {
                        System.out.println("Usage: update <orderId> <status>");
                    }
                    break;
                case "delete":
                    if (command.length > 1) {
                        orderManager.tell(new DeleteOrder(command[1]), ActorRef.noSender());
                    } else {
                        System.out.println("Usage: delete <orderId>");
                    }
                    break;
                case "list":
                    orderManager.tell(new ListOrders(), ActorRef.noSender());
                    break;
                case "exit":
                    system.terminate();
                    return;
                default:
                    System.out.println("Unknown command. Use create, update, delete, list, or exit.");
                    break;
            }
        }
    }
}
