package dk.g4.st25.common.util;

public class Product {
    private String id;
    private String type;
    public Product(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public Product(String type) {
        this.type = type;
    }
    public String getId() {
        return id;
    }
    public String getType() {
        return type;
    }

}
