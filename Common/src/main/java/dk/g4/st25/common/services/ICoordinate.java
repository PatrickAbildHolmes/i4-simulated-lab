package dk.g4.st25.common.services;

import dk.g4.st25.common.util.Order;

public interface ICoordinate {
    // Icoordinate interface for the coordinator class
    // Starts production from core
    int startProduction(Order order);

    int getProduced();

}
