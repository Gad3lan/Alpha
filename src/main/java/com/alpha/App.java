package com.alpha;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCharacterCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * JavaFX App
 */
public class App extends Application {
    public enum AlphaColor {
        RGB,
        R,
        G,
        B
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("primary.fxml"));
        Locale loc;
        if (System.getProperty("user.language").equals("fr")) {
            loc = new Locale("fr");
        } else {
            loc = new Locale("en");
        }
        fxmlLoader.setResources(ResourceBundle.getBundle("com.alpha.trad", loc));
        Parent root = fxmlLoader.load();
        Controller ctrl = fxmlLoader.getController();
        Scene scene = new Scene(root, 700, 600);
        primaryStage.setTitle("Alpha");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(600);
        primaryStage.show();


        scene.setOnKeyPressed(new EventHandler<>() {
            final KeyCombination undoComb = new KeyCharacterCombination("z", KeyCombination.SHORTCUT_DOWN);
            final KeyCombination redoComb = new KeyCharacterCombination("z", KeyCombination.SHIFT_DOWN, KeyCombination.SHORTCUT_DOWN);
            final KeyCombination resetComb = new KeyCharacterCombination("r", KeyCombination.SHORTCUT_DOWN);

            @Override
            public void handle(KeyEvent keyEvent) {
                if (undoComb.match(keyEvent)) {
                    ctrl.undo();
                    keyEvent.consume();
                } else if (redoComb.match(keyEvent)) {
                    ctrl.redo();
                    keyEvent.consume();
                } else if (resetComb.match(keyEvent)) {
                    ctrl.reset();
                    keyEvent.consume();
                }
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}