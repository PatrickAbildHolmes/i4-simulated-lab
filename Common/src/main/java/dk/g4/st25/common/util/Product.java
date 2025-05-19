package dk.g4.st25.common.util;

import java.lang.reflect.Array;

public class Product {
    private String id;
    private String type;
    private Part[] parts;
    public Product(String id, String type, Part[] parts) {
        this.id = id;
        this.type = type;
        this.parts = parts;
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
    public Part[] getParts() { return parts; }
}
