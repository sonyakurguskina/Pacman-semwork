package ru.itis.kurguskina;
import ru.itis.kurguskina.client.Client;
import javafx.fxml.FXML;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.application.Platform;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class Controller implements EventHandler<KeyEvent> {
    final private static double frames_in_second = 5.0;
    @FXML private Label gameOver;
    @FXML private Label score;
    @FXML private Label level;
    @FXML private PacManMapping pacManMapping;
    private static final String[] levelFiles = {"src/ru/itis/kurguskina/levels/level1.txt","src/ru/itis/kurguskina/levels/level2.txt", "src/ru/itis/kurguskina/levels/level3.txt"};
    private GameLogic gameLogic;
    private Client client;
    private Timer timer;
    private static int weakGhostModeCounter;
    private boolean paused;

    public Controller() {
        this.paused = false;
    }

    public void setPacManModel(GameLogic gameLogic) {
        this.gameLogic = gameLogic;
    }

    public void initialize() {
        String file = this.getLevelFile(0);
        try {
            this.gameLogic = Init();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.update(GameLogic.Direction.NONE);
        weakGhostModeCounter = 25;
        this.runTimer();
    }

    private GameLogic Init() throws IOException {
        client = new Client();
        GameLogic gameLogic = new GameLogic();
        client.setPacManModel(gameLogic);
        client.start();
        return gameLogic;
    }

    private void runTimer() {
        this.timer = new java.util.Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                Platform.runLater(new Runnable() {
                    public void run() {
                        update(gameLogic.getCurrentDirection());
                    }
                });
            }
        };

        long frametimeMS = (long)(1000.0 / frames_in_second);
        this.timer.schedule(task, 0, frametimeMS);
    }

    private void update(GameLogic.Direction direction) {
        this.gameLogic.step(direction);
        this.pacManMapping.update(gameLogic);
        this.score.setText(String.format("Score: %d", this.gameLogic.getScoreCount()));
        this.level.setText(String.format("Level №%d", this.gameLogic.getLevelNumber()));
        if (gameLogic.isIsGameOver()) {
            this.gameOver.setText(String.format("GAME OVER"));
            pause();
        }
        if (gameLogic.isIsWon()) {
            this.gameOver.setText(String.format("YOU WON!"));
        }
        if (gameLogic.isIsWeakGhostMode()) {
            weakGhostModeCounter--;
        }
        if (weakGhostModeCounter == 0 && gameLogic.isIsWeakGhostMode()) {
            gameLogic.setWeakGhostMode(false);
        }
    }

    @Override
    public void handle(KeyEvent keyEvent) {
        boolean keyRecognized = true;
        KeyCode code = keyEvent.getCode();

        Boolean isSend = client.sendMessage(code);

        if (code == KeyCode.G) {
            pause();
            this.gameLogic.startNewGame();
            this.gameOver.setText(String.format(""));
            paused = false;
            this.runTimer();
        }

        if (isSend) {
            keyEvent.consume();
        }
    }


    public void pause() {
            this.timer.cancel();
            this.paused = true;
    }


    public static int getWeakGhostModeCounter() {
        return weakGhostModeCounter;
    }

    public static String getLevelFile(int x)
    {
        return levelFiles[x];
    }

    public double getBoardWidth() {
        return PacManMapping.cell_width * this.pacManMapping.getColumnsCount();
    }

    public double getBoardHeight() {
        return PacManMapping.cell_width * this.pacManMapping.getRowsCount();
    }

    public static void setWeakGhostModeCounter() {
        weakGhostModeCounter = 30;
    }


}
