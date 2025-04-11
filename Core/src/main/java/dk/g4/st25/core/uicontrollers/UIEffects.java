package dk.g4.st25.core.uicontrollers;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;

public class UIEffects {

    // This class handles UI effects that can be initialized throughout the different scenes.
    // Currently only has functionality for applying a hovering effect.
    private static final double upScaled = 1.2;
    private static final double normalScale = 1.0;

    public static void applyHoverEffect(Node node) {
        node.setOnMouseEntered(event -> scaleNode(node, upScaled));
        node.setOnMouseExited(event -> scaleNode(node, normalScale));
    }

    private static void scaleNode(Node node, double scale) {
        node.setScaleX(scale);
        node.setScaleY(scale);
    }

}