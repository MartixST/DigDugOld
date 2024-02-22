package lab;

import javafx.scene.image.Image;

public class SandTile {
    private Image texture;

    private double x, y;

    public SandTile(double x, double y, Image texture) {
        this.x = x;
        this.y = y;
        this.texture = texture;
    }

    public Image getTexture() {
        return texture;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
