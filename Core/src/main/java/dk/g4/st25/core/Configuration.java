package dk.g4.st25.core;

import dk.g4.st25.common.services.ICoordinate;
import dk.g4.st25.common.services.IExecuteCommand;
import dk.g4.st25.common.services.IMonitorStatus;

import java.sql.SQLOutput;
import java.util.Collection;
import java.util.List;
import java.util.ServiceLoader;

import static java.util.stream.Collectors.toList;

public class Configuration {

    public Configuration() {}

    public List<IMonitorStatus> getIMonitorStatusImplementationsList () {
        return ServiceLoader.load(IMonitorStatus.class).stream().map(ServiceLoader.Provider::get).collect(toList());
    }

    public List<ICoordinate> getICoordinateImplementationsList () {
        return ServiceLoader.load(ICoordinate.class).stream().map(ServiceLoader.Provider::get).collect(toList());
    }
}