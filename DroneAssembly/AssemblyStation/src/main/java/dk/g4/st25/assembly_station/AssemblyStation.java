package dk.g4.st25.assembly_station;

import dk.g4.st25.common.machine.MachineSPI;

public class AssemblyStation implements MachineSPI {
    @Override
    public Object taskCompletion() {
        return null;
    }

    @Override
    public int productionCompletion() {
        return 0;
    }
}
