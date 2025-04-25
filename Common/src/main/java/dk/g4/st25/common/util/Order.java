package dk.g4.st25.common.util;

public class Order {
    private int id;
    private int amount;
    private Product product;
    private Status status;
    private enum Status {
        IN_QUEUE,
        BEING_PROCESSED,
        FINISHED
    }
    public Order(int id, Product product, int amount) {
        this.id = id;
        this.product = product;
        this.amount = amount;
        this.status = Status.IN_QUEUE;
    }
}
