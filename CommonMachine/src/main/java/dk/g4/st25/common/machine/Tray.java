package dk.g4.st25.common.machine;

public class Tray {
    private boolean available;
    private Object content; // Can be Product or Part
    Tray() {
        this.available = true;
        this.content = null;
    }

    public void setContent(Object content) {
        this.content = content;
    }
    public Object getContent() {
        return content;
    }
    public boolean isAvailable() {
        return available;
    }
    public void setAvailable(boolean available) {
        this.available = available;
    }
}
