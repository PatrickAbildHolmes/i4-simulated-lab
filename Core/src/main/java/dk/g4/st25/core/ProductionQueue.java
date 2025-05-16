package dk.g4.st25.core;

import dk.g4.st25.common.services.ICoordinate;
import dk.g4.st25.common.util.Order;

import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;

public class ProductionQueue {
    private final static ProductionQueue productionQueue = new ProductionQueue();
    private Queue<Order> orders = new LinkedList<>();
    private boolean productionStarted = false;

    private ProductionQueue() {}

    public void start() {
        productionStarted = true;

        ICoordinate coordinator = App.getAppContext().getICoordinateImplementations().stream().findAny().get();

        while (!orders.isEmpty()) {
            Order nextItem = orders.remove(); // retrieves and removes head of queue
            coordinator.startProduction(nextItem);
        }
        productionStarted = false;
    }

    public void add(Order order) {
        orders.add(order);
    }

    public boolean isProductionStarted() {
        return productionStarted;
    }

    public Queue<Order> getOrders() {
        return orders;
    }

    static public ProductionQueue getInstance() {
        return productionQueue;
    }
}
