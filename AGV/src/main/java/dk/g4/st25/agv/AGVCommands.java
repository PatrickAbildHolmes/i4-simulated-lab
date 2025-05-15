package dk.g4.st25.agv;

public enum AGVCommands {
    MOVECHARGER("MoveToChargerOperation"),
    MOVEASSEMBLY("MoveToAssemblyOperation"),
    MOVESTORAGE("MoveToStorageOperation"),
    PUTASSEMBLY("PutAssemblyOperation"),
    PICKASSEMBLY("PickAssemblyOperation"),
    PICKWAREHOUSE("PickWarehouseOperation"),
    PUTWAREHOUSE("PutWarehouseOperation"),
    GETSTATUS("getStatus");

    private final String commandString;

    AGVCommands(String commandString){
        this.commandString = commandString;
    }

    public String getCommandString() {
        return commandString;
    }
}
