package dk.g4.st25.common.machine;

import dk.g4.st25.common.services.ICoordinate;
import dk.g4.st25.common.util.Order;

import java.util.ArrayList;
import java.util.List;

public class Coordinator implements ICoordinate {
    private final List<Object> objectList = new ArrayList<Object>();


    @Override
    public int startProduction(Order order) {
        System.out.println("Starting production...: " + order.getName());
        return 0;
    }

    @Override
    public List<Object> getObjectList() {
        return objectList;
    }
}
