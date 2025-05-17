package dk.g4.st25.common.services;

import dk.g4.st25.common.util.Order;

import java.util.List;

public interface ICoordinate {
    // Icoordinate interface for the coordinator class
    // Starts production from core
    int startProduction(Order order);

    List<Object> getObjectList();

    int getProduced();

}
