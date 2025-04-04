package dk.g4.st25.common.machine;

public interface MachineSPI {
    // Signals that a task is complete
    Object taskCompletion();


    // Signals when all tasks relating to a production are complete
    int productionCompletion();
}
