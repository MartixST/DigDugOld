package lab;

import javafx.geometry.Rectangle2D;

public class Pump {
    private Rectangle2D area;

    private static final int PUMP_WIDTH = 40;
    private static final int PUMP_HEIGHT = 40;
    private int x, y;

    public Pump(int x, int y, String playerDirection, int pumpLength) {
        this.x = x;
        this.y = y;
        initializeArea(playerDirection, pumpLength);
    }

    private void initializeArea(String playerDirection, int pumpLength) {
        switch (playerDirection) {
            case "Left":
                area = new Rectangle2D(x - pumpLength, y, pumpLength, PUMP_HEIGHT);
                break;
            case "Right":
                area = new Rectangle2D(x + 40, y, pumpLength, PUMP_HEIGHT);
                break;
            case "Up":
                area = new Rectangle2D(x, y - pumpLength, PUMP_WIDTH, pumpLength);
                break;
            case "Down":
                area = new Rectangle2D(x, y + 40, PUMP_WIDTH, pumpLength);
                break;
            default:
                area = new Rectangle2D(x, y, pumpLength, PUMP_HEIGHT);
                break;
        }
    }


    public Rectangle2D getArea() {
        return area;
    }
}

