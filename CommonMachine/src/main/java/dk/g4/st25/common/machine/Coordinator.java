package dk.g4.st25.common.machine;

import dk.g4.st25.common.services.ICoordinate;
import dk.g4.st25.common.util.Order;

public class Coordinator implements ICoordinate {
    @Override
    public int startProduction(Order order) {
        System.out.println("Starting production...: " + order.getName());
        return 0;
    }
}
