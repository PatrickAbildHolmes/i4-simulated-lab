package dk.g4.st25.common.services;

import java.util.ArrayList;

public interface IScheduleProduction {
    // Productions are controlled on a control panel (GUI) level,
    // and items are controlled on machine level
    int addProductionToQueue(String product, int amount, ArrayList<Object> itemsNeeded);

    // The object(s) that is returned is a ProductionQueue,
    // which is a list of Strings containing amount and product type for every/the-specific order
    ArrayList<Object> getProductionQueue();
    Object getProductionQueue(String id);

}
