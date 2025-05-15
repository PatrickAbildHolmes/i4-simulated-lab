package dk.g4.st25.core;

import dk.g4.st25.common.services.IExecuteCommand;
import dk.g4.st25.common.services.IMonitorStatus;

import java.sql.SQLOutput;
import java.util.Collection;
import java.util.List;
import java.util.ServiceLoader;

import static java.util.stream.Collectors.toList;

public class Configuration {

    public Configuration() {}

    public Collection<? extends IExecuteCommand> getIExecuteCommandImplementationsList() {
        System.out.println(ServiceLoader.load(IExecuteCommand.class));
        System.out.println("Count " + ServiceLoader.load(IExecuteCommand.class).stream().count());
        return ServiceLoader.load(IExecuteCommand.class).stream().map(ServiceLoader.Provider::get).collect(toList());
    }


    public List<IMonitorStatus> getIMonitorStatusImplementationsList () {
        System.out.println(ServiceLoader.load(IMonitorStatus.class));
        return ServiceLoader.load(IMonitorStatus.class).stream().map(ServiceLoader.Provider::get).collect(toList());
    }
}