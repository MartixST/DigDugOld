package lab;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Fygar implements Enemy {
    private Image standLeft, standRight;
    private Image runLeft, runRight;
    private Image deathBeginLeft, deathBeginMiddleLeft, deathEndMiddleLeft, deathEndLeft;
    private Image deathBeginRight, deathBeginMiddleRight, deathEndMiddleRight, deathEndRight;
    private Image currentImage;
    private Image pointsTexture;
    private Direction lastHorizontalDirection = Direction.RIGHT;
    private GameManager gameManager;
    private enum Direction {
        LEFT, RIGHT, UP, DOWN
    }

    private boolean isTextureToggle = false;
    private long lastMoveTime = 0;
    private static final long MOVE_DELAY = 170;
    private static final int MOVE_SPEED = 9;
    private int x, y;
    private static final int SIZE = GameField.TILE_SIZE;
    private boolean isDying = false;
    private int deathStage = 0;
    private long lastDeathStageTime = 0;
    private static final long DEATH_ANIMATION_DELAY = 200;
    private boolean isReviving = false;
    private long lastReviveStageTime = 0;
    private boolean showPoints = false;
    private long pointsDisplayTime = 0;
    private static final long POINTS_DISPLAY_DURATION = 1000;
    private int pointsX, pointsY;
    private boolean isDisplayingPoints = false;


    private Direction direction;

    public Fygar(int x, int y, GameManager gameManager) {
        this.x = x;
        this.y = y;

        standLeft = new Image(getClass().getResourceAsStream("/enemies/fygar_left_stand.png"));
        standRight = new Image(getClass().getResourceAsStream("/enemies/fygar_right_stand.png"));
        runLeft = new Image(getClass().getResourceAsStream("/enemies/fygar_left_run.png"));
        runRight = new Image(getClass().getResourceAsStream("/enemies/fygar_right_run.png"));
        deathBeginLeft = new Image(getClass().getResourceAsStream("/enemies/fygar_death_begin_left.png"));
        deathBeginMiddleLeft = new Image(getClass().getResourceAsStream("/enemies/fygar_death_begin_middle_left.png"));
        deathEndMiddleLeft = new Image(getClass().getResourceAsStream("/enemies/fygar_death_end_middle_left.png"));
        deathEndLeft = new Image(getClass().getResourceAsStream("/enemies/fygar_death_end_left.png"));
        deathBeginRight = new Image(getClass().getResourceAsStream("/enemies/fygar_death_begin_right.png"));
        deathBeginMiddleRight = new Image(getClass().getResourceAsStream("/enemies/fygar_death_begin_middle_right.png"));
        deathEndMiddleRight = new Image(getClass().getResourceAsStream("/enemies/fygar_death_end_middle_right.png"));
        deathEndRight = new Image(getClass().getResourceAsStream("/enemies/fygar_death_end_right.png"));

        currentImage = standRight;
        direction = Fygar.Direction.RIGHT;

        this.gameManager = gameManager;
    }

    public void update(GameField gameField) {
        long currentTime = System.currentTimeMillis();
        if (isDying) {
            if (currentTime - lastDeathStageTime > DEATH_ANIMATION_DELAY) {
                if (gameManager.getPlayer().isPumping()) {
                    if (deathStage < 4) {
                        deathStage++;
                        updateDeathTexture();
                    }
                } else {
                    startReviving();
                }
                lastDeathStageTime = currentTime;
            }
        } else if (isReviving) {
            if (currentTime - lastReviveStageTime > DEATH_ANIMATION_DELAY) {
                if (deathStage > 0) {
                    deathStage--;
                    updateReviveTexture();
                }
                lastReviveStageTime = currentTime;
                if (deathStage == 0) {
                    isReviving = false;
                }
            }
        } else {
            if (currentTime - lastMoveTime > MOVE_DELAY) {
                // запоминание последнего горизонтального направления перед вертикальным движением
                if (direction == Fygar.Direction.LEFT || direction == Fygar.Direction.RIGHT) {
                    lastHorizontalDirection = direction;
                }

                // при вертикальном движении используется текстура, соответствующая последнему горизонтальному направлению
                if (direction == Fygar.Direction.UP || direction == Fygar.Direction.DOWN) {
                    if (lastHorizontalDirection == Fygar.Direction.LEFT) {
                        currentImage = isTextureToggle ? standLeft : runLeft;
                    } else {
                        currentImage = isTextureToggle ? standRight : runRight;
                    }
                } else {
                    currentImage = (direction == Fygar.Direction.LEFT) ? (isTextureToggle ? standLeft : runLeft) : (isTextureToggle ? standRight : runRight);
                }
                isTextureToggle = !isTextureToggle;
                lastMoveTime = currentTime;

                move(gameField);
            }
        }
    }

    public void startDying() {
        if (!isDying && !isReviving) {
            isDying = true;
            deathStage = 0;
            lastDeathStageTime = System.currentTimeMillis();
        }
    }

    public void startReviving() {
        if (isDying) {
            isDying = false;
            isReviving = true;
            lastReviveStageTime = System.currentTimeMillis();
        }   
    }

    private void calculatePoints() {
        if (y <= GameField.SKY_HEIGHT * GameField.TILE_SIZE) {
            pointsTexture = gameManager.getPoints200Texture();
            gameManager.addScore(200);
        } else if (y <= (GameField.SKY_HEIGHT + GameField.MID_HEIGHT) * GameField.TILE_SIZE) {
            pointsTexture = gameManager.getPoints300Texture();
            gameManager.addScore(300);
        } else {
            pointsTexture = gameManager.getPoints500Texture();
            gameManager.addScore(500);
        }
    }

    private void updateReviveTexture() {
        switch (deathStage) {
            case 3: currentImage = deathEndMiddleLeft; break;
            case 2: currentImage = deathBeginMiddleLeft; break;
            case 1: currentImage = deathBeginLeft; break;
            case 0: currentImage = standLeft; break;
            default: currentImage = standLeft; break;
        }
    }


    private void move(GameField gameField) {
        int newX = x, newY = y;
        switch (direction) {
            case LEFT:
                newX -= MOVE_SPEED;
                break;
            case RIGHT:
                newX += MOVE_SPEED;
                break;
            case UP:
                newY -= MOVE_SPEED;
                break;
            case DOWN:
                newY += MOVE_SPEED;
                break;
        }

        if (canMove(newX, newY, gameField)) {
            x = newX;
            y = newY;
        } else {
            changeDirection(gameField);
        }
    }

    private boolean canMove(int newX, int newY, GameField gameField) {
        if (newX < 0 || newX + SIZE > gameField.getWidth() || newY < (GameField.SKY_HEIGHT * GameField.TILE_SIZE) - SIZE || newY + SIZE > gameField.getHeight() + 4) {
            return false;
        }
        return gameField.isPathClear(newX, newY);
    }

    private void updateDeathTexture() {
        switch (deathStage) {
            case 1: currentImage = deathBeginLeft; break;
            case 2: currentImage = deathBeginMiddleLeft; break;
            case 3: currentImage = deathEndMiddleLeft;
                pointsX = x;
                pointsY = y;
                isDisplayingPoints = true;
                calculatePoints();
                pointsDisplayTime = System.currentTimeMillis(); break;
            case 4: currentImage = deathEndLeft; break;
            default: currentImage = standLeft; break;
        }
    }

    private void changeDirection(GameField gameField) {
        List<Fygar.Direction> possibleDirections = new ArrayList<>();
        if (direction != Fygar.Direction.LEFT) possibleDirections.add(Fygar.Direction.RIGHT);
        if (direction != Fygar.Direction.RIGHT) possibleDirections.add(Fygar.Direction.LEFT);
        if (direction != Fygar.Direction.UP) possibleDirections.add(Fygar.Direction.DOWN);
        if (direction != Fygar.Direction.DOWN) possibleDirections.add(Fygar.Direction.UP);

        possibleDirections.removeIf(dir -> !canMoveInDirection(dir, gameField));

        if (possibleDirections.isEmpty()) {
            direction = getOppositeDirection(direction);
        } else {
            Random random = new Random();
            direction = possibleDirections.get(random.nextInt(possibleDirections.size()));
        }
    }

    private Fygar.Direction getOppositeDirection(Fygar.Direction currentDirection) {
        switch (currentDirection) {
            case LEFT: return Fygar.Direction.RIGHT;
            case RIGHT: return Fygar.Direction.LEFT;
            case UP: return Fygar.Direction.DOWN;
            case DOWN: return Fygar.Direction.UP;
            default: return currentDirection;
        }
    }

    private boolean canMoveInDirection(Fygar.Direction direction, GameField gameField) {
        int testX = x, testY = y;
        switch (direction) {
            case LEFT:
                testX -= MOVE_SPEED;
                break;
            case RIGHT:
                testX += MOVE_SPEED;
                break;
            case UP:
                testY -= MOVE_SPEED;
                break;
            case DOWN:
                testY += MOVE_SPEED;
                break;
        }
        return canMove(testX, testY, gameField);
    }

    public void draw(GraphicsContext gc) {
        if (!isDisplayingPoints) {
            gc.drawImage(currentImage, x - 5, y - 5, SIZE, SIZE);
        } else {
            gc.drawImage(pointsTexture, pointsX, pointsY, SIZE, SIZE);
        }
    }
    public boolean intersects(Rectangle2D area) {
        return area.intersects(x, y, SIZE, SIZE);
    }

    public boolean isDeathAnimationComplete() {
        return isDying && deathStage >= 4;
    }
}
