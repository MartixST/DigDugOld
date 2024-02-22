package lab;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class DigDugGame extends Application {
    private Canvas canvas;
    private GameManager gameManager;
    private Player player;

    private boolean movingLeft, movingRight, movingUp, movingDown;


    @Override
    public void start(Stage primaryStage) {
        try {
        /*
            FXMLLoader loader = new FXMLLoader(getClass().getResource("GameView.fxml"));
            Parent rootTwo = loader.load();
            primaryStage.setScene(new Scene(rootTwo));

            this.gameManager = new GameManager(canvas);
            this.gameManager.initialize();
        */
            Group root = new Group();
            canvas = new Canvas(600, 600);
            root.getChildren().add(canvas);



            GameField gameField = new GameField();
            gameManager = new GameManager(canvas);

            player = new Player(280, 350, movingLeft, movingRight, movingUp, movingDown, gameField, gameManager);
            gameManager.setPlayer(player);


            Scene scene = new Scene(root, 600, 600);
            scene.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.SPACE) {
                    System.out.println("Space pressed");
                    player.activatePump();
                }
                handleKeyPress(e.getCode());
            });

            scene.setOnKeyReleased(e -> {
                if (e.getCode() == KeyCode.SPACE) {
                    player.deactivatePump();
                }
                handleKeyRelease(e.getCode());
            });
            primaryStage.setScene(scene);
            primaryStage.setTitle("Dig Dug");
            primaryStage.show();

            new AnimationTimer() {
                public void handle(long currentNanoTime) {
                    update();
                    gameManager.update();
                    gameManager.draw(canvas.getGraphicsContext2D());
                }
            }.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleKeyPress(KeyCode code) {
        if (player.isPumping()) return;
        switch (code) {
            case LEFT:
                movingLeft = true;
                break;
            case RIGHT:
                movingRight = true;
                break;
            case UP:
                movingUp = true;
                break;
            case DOWN:
                movingDown = true;
                break;
            case SPACE:
                player.activatePump();
                movingLeft = movingRight = movingUp = movingDown = false;
                break;
            default:
                break;
        }
    }


    private void handleKeyRelease(KeyCode code) {
        if (player.isPumping() && code != KeyCode.SPACE) return;
        switch (code) {
            case LEFT:
                movingLeft = false;
                break;
            case RIGHT:
                movingRight = false;
                break;
            case UP:
                movingUp = false;
                break;
            case DOWN:
                movingDown = false;
                break;
            case SPACE:
                player.deactivatePump();
                break;
            default:
                break;
        }
        player.stop();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void update() {
        Player player = gameManager.getPlayer();
        if (player != null) {
            if (movingLeft) player.moveLeft();
            if (movingRight) player.moveRight();
            if (movingUp) player.moveUp();
            if (movingDown) player.moveDown();
        }
    }
}
