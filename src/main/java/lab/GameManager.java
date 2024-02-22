package lab;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GameManager {
    private GameField gameField;
    private Image points200, points300, points500;
    private Player player;
    private Canvas canvas;
    private List<Enemy> enemies;
    private Image imageOfLife;
    private ScoreDAO scoreDAO;
    private GraphicsContext gc;

    @FXML
    private Canvas gameCanvas;
    @FXML private TextField playerName;
    @FXML private Button startButton;
    @FXML private Button saveScore;
    @FXML private TableView<Score> tableView;


    private AnimationTimer gameLoop;
    private boolean isGameStarted = false;
    private int totalScore = 0;
    private String playerNickName;

    public GameManager() {}

    public GameManager(Canvas canvas) {
        this.canvas = canvas;
        this.gameField = new GameField();
        this.enemies = new ArrayList<>();

        // Добавляем врагов в общий список
        enemies.add(new Pooka(80, 240, this));
        enemies.add(new Fygar(480, 160, this));
        enemies.add(new Fygar(400, 480, this));
        enemies.add(new Pooka(360, 240, this));
        enemies.add(new Pooka(200, 440, this));

        imageOfLife = new Image(getClass().getResourceAsStream("/character/stand_left.png"));
        points200 = new Image(getClass().getResourceAsStream("/background/200points.png"));
        points300 = new Image(getClass().getResourceAsStream("/background/300points.png"));
        points500 = new Image(getClass().getResourceAsStream("/background/500points.png"));

        scoreDAO = new ScoreDAO();
    }

    @FXML
    public void initialize() {
        gc = gameCanvas.getGraphicsContext2D();
        drawSplashScreen();
    }

    private void drawSplashScreen() {
        // Загрузка и отрисовка заставки
        Image splashImage = new Image("/background/digdug.png");
        gc.drawImage(splashImage, 0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
    }

    private void clearCanvas() {
        gc.clearRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
        // Дополнительный код для очистки холста
    }

    @FXML
    private void handleStartButtonAction() {
        if (!isGameStarted) {
            startGame();
        }
    }

    private void startGame() {
        clearCanvas();
        gameCanvas.setVisible(false);

        playerNickName = playerName.getText();
        playerName.setDisable(true);

        startButton.setVisible(false);

        isGameStarted = true;

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };
        gameLoop.start();
    }

    public void update() {
        if (!player.isAlive()) {
            return;
        }

        player.update(gameField, enemies);

        Iterator<Enemy> enemyIterator = enemies.iterator();
        while (enemyIterator.hasNext()) {
            Enemy enemy = enemyIterator.next();
            enemy.update(gameField);

            if (enemy.isDeathAnimationComplete()) {
                enemyIterator.remove();
            }
        }

        if (player.isPumping()) {
            checkPumpCollision();
        }
    }

    private void checkPumpCollision() {
        if (player.isPumping() && player.getPump() != null) {
            Rectangle2D pumpArea = player.getPump().getArea();

            for (Enemy enemy : enemies) {
                if (enemy.intersects(pumpArea)) {
                    if (player.isPumping()) {
                        enemy.startDying();
                    } else {
                        enemy.startReviving();
                    }
                }
            }
        }
    }

    public void draw(GraphicsContext gc) {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
        gameField.draw(gc);
        for (int i = 0; i < player.getLives(); i++) {
            gc.drawImage(imageOfLife, 500 + (i * 25), 10, 40, 40);
        }

        gc.setFill(Color.WHITE);
        gc.setFont(new Font("Arial", 50));
        gc.fillText("Score: " + totalScore, (canvas.getWidth() - 170) / 2, 50);

        if (player.isAlive()) {
            player.draw(gc);
            for (Enemy enemy : enemies) {
                enemy.draw(gc);
            }
        } else {
            gc.setFill(Color.WHITE);
            gc.setFont(new Font("Arial", 50));
            gc.fillText("Game Over", (canvas.getWidth()+50) / 4, (canvas.getHeight()+50) / 2);
        }
    }

    public void addScore(int score) {
        totalScore += score;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Image getPoints200Texture() {
        return points200;
    }

    public Image getPoints300Texture() {
        return points300;
    }

    public Image getPoints500Texture() {
        return points500;
    }
}
