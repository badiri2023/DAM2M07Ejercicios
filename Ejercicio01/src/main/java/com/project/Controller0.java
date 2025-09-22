package com.project;

import org.w3c.dom.Text;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

public class Controller0 {


   @FXML
    private TextField nameTextArea,ageTextArea;

    @FXML
    private Button button1;

    @FXML
    private AnchorPane container;

    @FXML
    private Text errorTextMessage;

    @FXML
    private void comprobardor(ActionEvent event) {
        String ageText = valueOf(inputAge.getText().trim());
        String name = inputName.getText().trim();

        if (ageText.isEmpty()||name.isEmpty()){
            errorTextMessage.setData("El nombre y edad no pueden estar vacios");
        }else{
            int age=Integer.parseInt(ageTextArea);
            if (age<17){
                errorTextMessage.setData("La edad no puede ser menor a 18!!");
            }
            else{
                // Guardamos en Main
                Main.name = name;
                Main.age = age;
                UtilsViews.setView("View1");
            }
        }
    }

    @FXML
    private void toView0(ActionEvent event) {
        UtilsViews.setView("View0");
    }

    @FXML
    private void toView1(ActionEvent event) {
        UtilsViews.setView("View1");
    }

    @FXML
    private void animateToView0(ActionEvent event) {
        UtilsViews.setViewAnimating("View0");
    }

    @FXML
    private void animateToView1(ActionEvent event) {
        UtilsViews.setViewAnimating("View1");
    }
}