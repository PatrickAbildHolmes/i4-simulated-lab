package dk.g4.st25.common.machine;

public interface MachineSPI {
    enum systemState{

    }

    /**
     * Signals if a task from a machine has been completed
     * @return is of integer, for expansion, as the coordinator could be able to interpret further codes
     */
    int taskCompletion();


    // Signals when all tasks relating to a production are complete
    int productionCompletion();
}
