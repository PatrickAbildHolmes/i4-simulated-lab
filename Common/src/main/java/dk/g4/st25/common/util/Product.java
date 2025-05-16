package dk.g4.st25.common.util;

public class Product {
    private int id;
    private String type;
    public Product(int id, String type) {
        this.id = id;
        this.type = type;
    }

    public Product(String type) {
        this.type = type;
    }
    public int getId() {
        return id;
    }
    public String getType() {
        return type;
    }

}
