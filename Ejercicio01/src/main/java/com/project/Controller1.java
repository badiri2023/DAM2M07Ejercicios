package com.project;

import javafx.scene.text.Text;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public class Controller1 {

    @FXML
    private Text saludoText;

   @FXML
    public void initialize() {
        saludoText.setText("Hola " +Main.name + ", tens " + Main.age + " anys!");
    }
    @FXML
    private void animateToView0(ActionEvent event) {
        UtilsViews.setViewAnimating("View0");
    }
}