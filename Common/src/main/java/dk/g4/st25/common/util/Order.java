package dk.g4.st25.common.util;

public class Order {
    private int id;
    private String name;
    private int amount;
    private Product product;
    private Status status;
    public enum Status {
        IN_QUEUE,
        BEING_PROCESSED,
        FINISHED
    }
    public Order(int id, String name, Product product, int amount) {
        this.id = id;
        this.name = name;
        this.product = product;
        this.amount = amount;
        this.status = Status.IN_QUEUE;
    }

    public int getId() {
        return id;
    }

    public int getAmount() {
        return amount;
    }

    public Product getProduct() {
        return product;
    }

    public String getName() {
        return name;
    }

    public Status getStatus() {
        return status;
    }
    public void setStatus(Status status) { this.status = status; }
}
