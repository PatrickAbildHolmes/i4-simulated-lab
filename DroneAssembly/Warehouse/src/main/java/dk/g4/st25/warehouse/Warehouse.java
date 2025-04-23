package dk.g4.st25.warehouse;

import dk.g4.st25.common.machine.MachineSPI;
import dk.g4.st25.common.protocol.ProtocolSPI;
import dk.g4.st25.common.services.IExecuteCommand;

import java.util.List;
import java.util.ServiceLoader;

import static java.util.stream.Collectors.toList;

public class Warehouse implements MachineSPI, IExecuteCommand {
    @Override
    public Object taskCompletion() {
        return null;
    }

    @Override
    public int productionCompletion() {
        return 0;
    }

    @Override
    public int sendCommand(String commandType, String commandParam, String endpoint) {
        List<ProtocolSPI> listOfProtocolImplementations = protocolSPIList();
        for (ProtocolSPI protocolImplementation : listOfProtocolImplementations) {

            if (protocolImplementation.getClass().getModule().getName().equals("SOAP")) {
                try {
                    protocolImplementation.readFrom(endpoint, commandParam);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("No protocol implementation named SOAP is found");
            }
        }
        return 0;
    }

    public List<ProtocolSPI> protocolSPIList() {
        return ServiceLoader.load(ProtocolSPI.class).stream().map(ServiceLoader.Provider::get).collect(toList());
    }
}
