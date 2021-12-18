package ru.itis.kurguskina;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("pacman.fxml"));
        Parent parent = loader.load();
        primaryStage.setTitle("PacMan");
        Controller controller = loader.getController();

        parent.setOnKeyPressed(controller);
        double boardWidth = controller.getBoardWidth() + 20.0;
        double boardHeight = controller.getBoardHeight() + 100.0;
        primaryStage.setScene(new Scene(parent, boardWidth, boardHeight));
        primaryStage.show();
        parent.requestFocus();
    }
}
