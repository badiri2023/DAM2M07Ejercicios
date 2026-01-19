package com.project;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    private Scene scene;
    private Parent pcRoot;
    private Parent movilRoot; // Esta será la vista del *menú principal*
    
    // Controladores
    private CtrlPc pcController;
    private CtrlMovilMain movilMainController; 

    private static final double BREAKPOINT = 600.0;
    private boolean isMovilView = false;

    @Override
    public void start(Stage primaryStage) throws Exception {

        DataManager dataManager = new DataManager();

        // Cargar la vista PC
        FXMLLoader pcLoader = new FXMLLoader(getClass().getResource("/assets/pcView.fxml"));
        pcRoot = pcLoader.load();
        pcController = pcLoader.getController(); 
        pcController.setDataManager(dataManager); 

        // Cargar la vista Móvil
        FXMLLoader movilLoader = new FXMLLoader(getClass().getResource("/assets/movilMainView.fxml"));
        movilRoot = movilLoader.load();
        movilMainController = movilLoader.getController(); 
        movilMainController.setDataManager(dataManager);
        
        double initialWidth = 800;
        double initialHeight = 600;

        Parent initialRoot;
        if (initialWidth < BREAKPOINT) {
            initialRoot = movilRoot;
            isMovilView = true;
        } else {
            initialRoot = pcRoot;
            isMovilView = false;
        }

        scene = new Scene(initialRoot, initialWidth, initialHeight);
        primaryStage.setScene(scene);
        primaryStage.setTitle("NintendoDB");

        primaryStage.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            if (newWidth.doubleValue() < BREAKPOINT && !isMovilView) {
                scene.setRoot(movilRoot);
                isMovilView = true;
            } else if (newWidth.doubleValue() >= BREAKPOINT && isMovilView) {
                scene.setRoot(pcRoot);
                isMovilView = false;
            }
        });

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
