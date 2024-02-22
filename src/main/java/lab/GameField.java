package lab;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;

import java.util.ArrayList;

public class GameField {
    private Image skyTexture;
    private Image blackTexture;
    private Image lightSandTexture;
    private Image lightBrownSandTexture;
    private Image darkBrownSandTexture;
    private Image darkSandTexture;
    private Image flowerTexture;
    private ArrayList<SandTile> sandTiles;

    private boolean[][] removedSandTiles;
    private static final int WIDTH = 15;
    private static final int HEIGHT = 15;
    public static final int TILE_SIZE = 40;
    public static final int SKY_HEIGHT = HEIGHT / 5;
    public static final int MID_HEIGHT = HEIGHT / 3;
    double tileWidth = 9;
    double tileHeight = 9;



    public GameField() {
        skyTexture = new Image(getClass().getResourceAsStream("/background/sky.png"));
        blackTexture = new Image(getClass().getResourceAsStream("/background/black.png"));
        lightSandTexture = new Image(getClass().getResourceAsStream("/sand/light_sand.png"));
        lightBrownSandTexture = new Image(getClass().getResourceAsStream("/sand/light_brown_sand.png"));
        darkBrownSandTexture = new Image(getClass().getResourceAsStream("/sand/dark_brown_sand.png"));
        darkSandTexture = new Image(getClass().getResourceAsStream("/sand/dark_sand.png"));
        flowerTexture = new Image(getClass().getResourceAsStream("/background/flower.png"));
        initializeSandTiles();

        removedSandTiles = new boolean[WIDTH][HEIGHT];
        for (int i = 0; i < WIDTH; i++) {
            for (int j = 0; j < HEIGHT; j++) {
                removedSandTiles[i][j] = false;
                System.out.println("Init removedSandTiles[" + i + "][" + j + "] = " + removedSandTiles[i][j]);
            }
        }
    }

    private void initializeSandTiles() {
        sandTiles = new ArrayList<>();

        for (int i = 0; i < WIDTH * TILE_SIZE; i += tileWidth) {
            for (int j = SKY_HEIGHT * TILE_SIZE; j < HEIGHT * TILE_SIZE; j += tileHeight) {
                Image texture = lightSandTexture;
                if(i <= 160 && i >= 40 && j <= 280 && j >= 240 ){} //left top pooka
                else if(i <= 560 && i >= 440 && j <= 200 && j >= 160 ){} //right top fygar
                else if(i <= 360 && i >= 210 && j <= 385 && j >= 340 ){} //player
                else if(i <= 315 && i >= 260 && j <= 385 && j >= 0 ){} //player stolb
                else if(i <= 480 && i >= 360 && j <= 520 && j >= 480 ){} //right bottom fygar
                else if(i <= 400 && i >= 360 && j <= 320 && j >= 200 ){} //right top pooka
                else if(i <= 240 && i >= 200 && j <= 520 && j >= 400 ){} //right top pooka
                else if(j <= 245){
                    sandTiles.add(new SandTile(i, j, texture));
                }
                else if (j <= 360) {
                    texture = lightBrownSandTexture;
                    sandTiles.add(new SandTile(i, j, texture));
                }
                else if (j <= 480) {
                    texture = darkBrownSandTexture;
                    sandTiles.add(new SandTile(i, j, texture));
                }
                else if (j <= 600) {
                    texture = darkSandTexture;
                    sandTiles.add(new SandTile(i, j, texture));
                }

            }
        }
    }
    public void drawBackground(GraphicsContext gc) {
        ImagePattern skyPattern = new ImagePattern(skyTexture, 0, 0, skyTexture.getWidth(), skyTexture.getHeight(), false);
        gc.setFill(skyPattern);
        gc.fillRect(0, 0, WIDTH * TILE_SIZE, SKY_HEIGHT * TILE_SIZE);

        ImagePattern blackPattern = new ImagePattern(blackTexture, 0, 0, blackTexture.getWidth(), blackTexture.getHeight(), true);
        gc.setFill(blackPattern);
        gc.fillRect(0, SKY_HEIGHT * TILE_SIZE, WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE);

        gc.drawImage(flowerTexture, 550, 90, 30, 30);
    }


    public void draw(GraphicsContext gc) {
        drawBackground(gc);
        for (SandTile tile : sandTiles) {
            gc.drawImage(tile.getTexture(), tile.getX(), tile.getY(), tileWidth, tileHeight);
        }
    }

    public boolean isPathClear(int x, int y) {
        int centerX = x + 30;
        int centerY = y + 30;

        for (SandTile tile : sandTiles) {
            if (centerX > tile.getX() && centerX < tile.getX() + GameField.TILE_SIZE &&
                    centerY > tile.getY() && centerY < tile.getY() + GameField.TILE_SIZE) {
                return false;
            }
        }
        return true;
    }


    public void removeSandTileAt(int playerX, int playerY) {
        int playerSize = GameField.TILE_SIZE;

        int playerLeft = playerX;
        int playerRight = playerX + playerSize;
        int playerTop = playerY;
        int playerBottom = playerY + playerSize;

        sandTiles.removeIf(tile ->
                playerRight > tile.getX() &&
                        playerLeft < tile.getX() + tileWidth &&
                        playerBottom > tile.getY() &&
                        playerTop < tile.getY() + tileHeight
        );


        int tileX = (int) Math.floor(playerX / GameField.TILE_SIZE);
        int tileY = (int) Math.floor(playerY / GameField.TILE_SIZE);

        if (tileX >= 0 && tileX < WIDTH && tileY >= 0 && tileY < HEIGHT) {
            removedSandTiles[tileX][tileY] = true;
        }
    }

    public int getWidth() {
        return WIDTH * TILE_SIZE;
    }

    public int getHeight() {
        return HEIGHT * TILE_SIZE;
    }
}