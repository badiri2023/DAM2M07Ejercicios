package com.project;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/assets/layout.fxml"));
        Scene scene = new Scene(loader.load());
        
        stage.setTitle("Ollama.exe");
        stage.setWidth(900);  
        stage.setHeight(700);
        stage.setScene(scene);
        stage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}