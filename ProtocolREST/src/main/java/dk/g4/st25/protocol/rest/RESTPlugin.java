package dk.g4.st25.protocol.rest;

import dk.g4.st25.common.protocols.Protocol;
import dk.g4.st25.common.protocols.ProtocolSPI;

public class RESTPlugin implements ProtocolSPI {
    @Override
    public int connectToMachine(Protocol protocol, String endpoint) {
        return 0;
    }

    @Override
    public int writeTo(String input, String endpoint) {
        return 0;
    }

    @Override
    public int readFrom(String endpoint) {
        return 0;
    }
}
