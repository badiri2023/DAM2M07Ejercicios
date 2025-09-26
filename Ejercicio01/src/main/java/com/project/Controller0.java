package com.project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

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
        String ageText = ageTextArea.getText().trim();
        String name = nameTextArea.getText().trim();

        if (ageText.isEmpty()||name.isEmpty()){
            errorTextMessage.setText("El nombre y edad no pueden estar vacios");
        }
        else{
            int age = Integer.parseInt(ageText);
            if (age<=0){
                errorTextMessage.setText("La edad no puede ser negativo!!");
            }
            else{
                // Guardamos en Main
                Main.name = name;
                Main.age = age;
              //  System.out.println("Guardado en Main, name: " + Main.name + ", age: " + Main.age);
                Controller1 controller1 = (Controller1) UtilsViews.getController("View1");
                controller1.initialize();
                UtilsViews.setView("View1");
            }
        }
    }

   /* @FXML
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
*/
    @FXML
    private void animateToView1(ActionEvent event) {
        UtilsViews.setViewAnimating("View1");
    }
}