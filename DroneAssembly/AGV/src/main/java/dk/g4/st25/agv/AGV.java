package dk.g4.st25.agv;

import dk.g4.st25.common.machine.MachineSPI;

public class AGV implements MachineSPI {
    @Override
    public Object taskCompletion() {
        return null;
    }

    @Override
    public int productionCompletion() {
        return 0;
    }
}
