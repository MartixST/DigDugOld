package lab;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.List;

public class    Player {
    private Image standUp, standDown, standLeft, standRight;
    private Image runUp, runDown, runLeft, runRight;
    private Image pumpDown, pumpLeft, pumpRight, pumpUp;
    private Image pumpPushDown, pumpPushRight, pumpPushUp, pumpPushLeft;
    private Image hoseDown, hoseLeft, hoseRight, hoseUp;
    private Image currentImage;
    private String currentImageId;
    private Image hoseImage;
    private Pump pump;
    GameField gameField;
    GameManager gameManager;

    private boolean movingLeft, movingRight, movingUp, movingDown;
    private long lastMoveTime = 0;
    private static final long MOVE_DELAY = 170;
    private static final int MOVE_SPEED = 10;
    private int x, y;
    private static final int SIZE = GameField.TILE_SIZE;
    private boolean isTextureToggle = false;
    private boolean isPumping = false;
    private long lastPumpTime = 0;
    private static final long PUMP_ANIMATION_DELAY = 200;
    private int hoseX, hoseY;
    private static final int PUMP_WIDTH = 30;
    private static final int PUMP_HEIGHT = 30;
    private int lives = 3;
    private boolean isAlive = true;
    private int hoseWidth, hoseHeight;




    public Player(int x, int y, boolean movingLeft, boolean movingRight, boolean movingUp, boolean movingDown, GameField gameField, GameManager gameManager) {
        this.x = x;
        this.y = y;

        standUp = new Image(getClass().getResourceAsStream("/character/stand_up.png"));
        standDown = new Image(getClass().getResourceAsStream("/character/stand_down.png"));
        standLeft = new Image(getClass().getResourceAsStream("/character/stand_left.png"));
        standRight = new Image(getClass().getResourceAsStream("/character/stand_right.png"));
        runUp = new Image(getClass().getResourceAsStream("/character/run_up.png"));
        runDown = new Image(getClass().getResourceAsStream("/character/run_down.png"));
        runLeft = new Image(getClass().getResourceAsStream("/character/run_left.png"));
        runRight = new Image(getClass().getResourceAsStream("/character/run_right.png"));
        hoseDown = new Image(getClass().getResourceAsStream("/character/hose_down.png"));
        hoseLeft = new Image(getClass().getResourceAsStream("/character/hose_left.png"));
        hoseRight = new Image(getClass().getResourceAsStream("/character/hose_right.png"));
        hoseUp = new Image(getClass().getResourceAsStream("/character/hose_up.png"));
        pumpDown = new Image(getClass().getResourceAsStream("/character/pump_down.png"));
        pumpLeft = new Image(getClass().getResourceAsStream("/character/pump_left.png"));
        pumpRight = new Image(getClass().getResourceAsStream("/character/pump_right.png"));
        pumpUp = new Image(getClass().getResourceAsStream("/character/pump_up.png"));
        pumpPushDown = new Image(getClass().getResourceAsStream("/character/pump_push_down.png"));
        pumpPushRight = new Image(getClass().getResourceAsStream("/character/pump_push_right.png"));
        pumpPushUp = new Image(getClass().getResourceAsStream("/character/pump_push_up.png"));
        pumpPushLeft = new Image(getClass().getResourceAsStream("/character/pump_push_left.png"));

        currentImage = standRight;
        currentImageId = "standRight";

        this.movingLeft = movingLeft;
        this.movingRight = movingRight;
        this.movingUp = movingUp;
        this.movingDown = movingDown;

        this.gameManager = gameManager;
        this.gameField = gameField;
    }

    public void moveLeft() {
        if (isPumping) return;
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastMoveTime > MOVE_DELAY) {
            int maxLeftMove = Math.min(MOVE_SPEED, x);
            x -= maxLeftMove;

            currentImage = isTextureToggle ? standLeft : runLeft;
            currentImageId = isTextureToggle ? "standLeft" : "runLeft";
            isTextureToggle = !isTextureToggle;
            lastMoveTime = currentTime;
        }
    }

    public void moveRight() {
        if (isPumping) return;
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastMoveTime > MOVE_DELAY) {
            int maxRightMove = Math.min(MOVE_SPEED, 600 - SIZE - x);
            x += maxRightMove;

            currentImage = isTextureToggle ? standRight : runRight;
            currentImageId = isTextureToggle ? "standRight" : "runRight";
            isTextureToggle = !isTextureToggle;
            lastMoveTime = currentTime;
        }
    }

    public void moveUp() {
        if (isPumping) return;
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastMoveTime > MOVE_DELAY) {
            if (y - MOVE_SPEED >= (GameField.SKY_HEIGHT * GameField.TILE_SIZE) - SIZE) {
                y -= MOVE_SPEED;
            }

            currentImage = isTextureToggle ? standUp : runUp;
            currentImageId = isTextureToggle ? "standUp" : "runUp";
            isTextureToggle = !isTextureToggle;
            lastMoveTime = currentTime;
        }
    }

    public void moveDown() {
        if (isPumping) return;
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastMoveTime > MOVE_DELAY) {
            if (y + MOVE_SPEED + SIZE <= 600) {
                y += MOVE_SPEED;
            }

            currentImage = isTextureToggle ? standDown : runDown;
            currentImageId = isTextureToggle ? "standDown" : "runDown";
            isTextureToggle = !isTextureToggle;
            lastMoveTime = currentTime;
        }
    }

    public void stop() {
        if (isPumping) {
            return;
        }
        if (!movingLeft && !movingRight && !movingUp && !movingDown) {
            if (currentImage == runRight) {
                currentImage = standRight;
                currentImageId = "standRight";
            } else if (currentImage == runLeft) {
                currentImage = standLeft;
                currentImageId = "standLeft";
            } else if (currentImage == runUp) {
                currentImage = standUp;
                currentImageId = "standUp";
            } else if (currentImage == runDown) {
                currentImage = standDown;
                currentImageId = "standDown";
            }
        }
    }

    public void draw(GraphicsContext gc) {
        gc.drawImage(currentImage, x, y, SIZE, SIZE);

        if (isPumping && hoseImage != null) {
            gc.drawImage(hoseImage, hoseX, hoseY, hoseWidth, hoseHeight);
            System.out.println("Drawing pump at (" + hoseX + ", " + hoseY + ")");
        }
    }

    public void update(GameField gameField, List<Enemy> enemies) {
        if (!isAlive) {
            return;
        }

        System.out.println("Updating player state. IsPumping: " + isPumping);
        if (isPumping) {
            movingLeft = movingRight = movingUp = movingDown = false;
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastPumpTime > PUMP_ANIMATION_DELAY) {
                togglePumpTextures();
                lastPumpTime = currentTime;
            }
        } else {
            gameField.removeSandTileAt(this.x, this.y);
        }

        checkCollisionsWithEnemies(enemies);
    }

    private void togglePumpTextures() {
        if (currentImageId.contains("Up")) {
            currentImage = (currentImage == pumpUp) ? pumpPushUp : pumpUp;
        } else if (currentImageId.contains("Down")) {
            currentImage = (currentImage == pumpDown) ? pumpPushDown : pumpDown;
        } else if (currentImageId.contains("Left")) {
            currentImage = (currentImage == pumpLeft) ? pumpPushLeft : pumpLeft;
        } else if (currentImageId.contains("Right")) {
            currentImage = (currentImage == pumpRight) ? pumpPushRight : pumpRight;
        }
    }

    private void checkCollisionsWithEnemies(List<Enemy> enemies) {
        for (Enemy enemy : enemies) {
            if (enemy.intersects(new Rectangle2D(x, y, SIZE, SIZE))) {
                die();
                return;
            }
        }
    }

    private void die() {
        lives--;
        if (lives <= 0) {
            isAlive = false;
        } else {
            respawn();
        }
    }

    private void respawn() {
        this.x = 280;
        this.y = 350;
        isPumping = false;
        movingLeft = movingRight = movingUp = movingDown = false;
    }

    private String getCurrentDirection() {
        if (currentImageId.contains("Left")) {
            return "Left";
        } else if (currentImageId.contains("Right")) {
            return "Right";
        } else if (currentImageId.contains("Up")) {
            return "Up";
        } else {
            return "Down";
        }
    }

    public void activatePump() {
        if (!isPumping && !movingLeft && !movingRight && !movingUp && !movingDown) {
            isPumping = true;
            lastPumpTime = System.currentTimeMillis();
            String currentDirection = getCurrentDirection();
            int maxPumpLength = 80;
            pump = new Pump(x, y, currentDirection, maxPumpLength);
            updatePumpTexture(currentDirection, maxPumpLength);
        }
    }


    private void updatePumpTexture(String currentDirection, int pumpLength) {
        switch (currentDirection) {
            case "Left":
                hoseImage = hoseLeft;
                hoseX = x - pumpLength;
                hoseY = y;
                hoseWidth = pumpLength;
                hoseHeight = PUMP_HEIGHT;
                break;
            case "Right":
                hoseImage = hoseRight;
                hoseX = x + SIZE;
                hoseY = y;
                hoseWidth = pumpLength;
                hoseHeight = PUMP_HEIGHT;
                break;
            case "Up":
                hoseImage = hoseUp;
                hoseX = x;
                hoseY = y - pumpLength;
                hoseWidth = PUMP_WIDTH;
                hoseHeight = pumpLength;
                break;
            case "Down":
                hoseImage = hoseDown;
                hoseX = x;
                hoseY = y + SIZE;
                hoseWidth = PUMP_WIDTH;
                hoseHeight = pumpLength;
                break;
        }
    }


    public void deactivatePump() {
        if (isPumping) {
            isPumping = false;
            pump = null;
            updateStandingTexture();
            System.out.println("Pump deactivated");
        }
    }

    private void updateStandingTexture() {
        if (currentImageId.contains("Left")) {
            currentImage = standLeft;
        } else if (currentImageId.contains("Right")) {
            currentImage = standRight;
        } else if (currentImageId.contains("Up")) {
            currentImage = standUp;
        } else if (currentImageId.contains("Down")) {
            currentImage = standDown;
        }
    }

    public Pump getPump() {
        return pump;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public int getLives() {
        return lives;
    }

    public boolean isPumping() {
        return isPumping;
    }
}