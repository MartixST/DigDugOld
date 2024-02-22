package lab;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;

public interface Enemy {
    void update(GameField gameField);
    boolean isDeathAnimationComplete();
    void startDying();
    void startReviving();
    boolean intersects(Rectangle2D area);
    void draw(GraphicsContext gc);
}