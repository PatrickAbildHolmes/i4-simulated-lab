package dk.g4.st25.common.machine;

public interface MachineSPI {
    enum systemState{
    }
    // Signals that a task is complete
    int taskCompletion();


    // Signals when all tasks relating to a production are complete
    int productionCompletion();
}
