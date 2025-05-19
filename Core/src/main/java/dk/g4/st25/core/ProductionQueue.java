package dk.g4.st25.core;

import dk.g4.st25.common.services.ICoordinate;
import dk.g4.st25.common.util.Order;
import dk.g4.st25.database.Database;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ProductionQueue {
    private static final ProductionQueue productionQueue = new ProductionQueue();
    private boolean productionStarted = false;

    private ProductionQueue() {}

    public void start() {
        productionStarted = true;

        // Get coordinator
        ICoordinate coordinator = App.getAppContext().getICoordinateImplementations().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No ICoordinate implementations found"));

        // Database
        Database db = Database.getDB();

        List<Order> orders = db.getOrders();
        while (!orders.isEmpty()) {
            // Start next order
            Order nextItem = orders.get(0);
            coordinator.startProduction(nextItem);

            // Delete order from db
            db.deleteOrder(nextItem.getId());

            // Update Orders (other orders might have been added)
            orders = db.getOrders();
        }
        productionStarted = false;
    }

    public boolean isProductionStarted() {
        return productionStarted;
    }

    static public ProductionQueue getInstance() {
        return productionQueue;
    }
}
