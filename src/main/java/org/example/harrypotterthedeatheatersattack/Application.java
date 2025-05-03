package org.example.harrypotterthedeatheatersattack;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class Application extends javafx.application.Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1920, 1080);

        stage.setTitle("Harry Potter - The Death Eaters Attack");

        Controller controller = fxmlLoader.getController();


        scene.setOnKeyPressed(controller::KeyPressed);
        scene.setOnKeyReleased(controller::KeyReleased);

        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(16), e -> controller.updateHarryPosition()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
