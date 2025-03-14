package dk.g4.st25.common.protocols;

public interface ProtocolSPI {

    int connectToMachine(Protocol protocol, String endpoint);

    int writeTo(String input, String endpoint);

    int readFrom(String endpoint);
}
