package dk.g4.st25.common.util;

public class Order {
    private String name;
    private int amount;
    private Product product;
    private Status status;
    private enum Status {
        IN_QUEUE,
        BEING_PROCESSED,
        FINISHED
    }
    public Order(String id, Product product, int amount) {
        this.name = id;
        this.product = product;
        this.amount = amount;
        this.status = Status.IN_QUEUE;
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }

    public Product getProduct() {
        return product;
    }

    public Status getStatus() {
        return status;
    }
}
