package dk.g4.st25.warehouse;

import dk.g4.st25.common.machine.MachineSPI;

public class Warehouse implements MachineSPI {
    @Override
    public int taskCompletion() {
        return 0;
    }

    @Override
    public int productionCompletion() {
        return 0;
    }
}
