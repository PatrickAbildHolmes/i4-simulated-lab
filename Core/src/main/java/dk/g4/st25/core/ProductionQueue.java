package dk.g4.st25.core;

import dk.g4.st25.common.services.ICoordinate;
import dk.g4.st25.common.util.Order;

import java.util.LinkedList;
import java.util.Queue;

public class ProductionQueue {
    private static ProductionQueue productionQueue = new ProductionQueue();
    private Queue<Order> orders = new LinkedList<>();
    private boolean productionStarted = false;

    private ProductionQueue() {}

    public void start() {
        productionStarted = true;

        Configuration conf = Configuration.get();
        ICoordinate coordinator = conf.coordinatorLoader();

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

    static public ProductionQueue getInstance() {
        return productionQueue;
    }
}
