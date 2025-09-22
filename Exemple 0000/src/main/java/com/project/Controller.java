package com.project;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public class Controller {
    // Botones de la calculadora
    @FXML
    private Button boton1;
    @FXML
    private Button boton2;
     @FXML
    private Button boton3;
     @FXML
    private Button boton4;
    @FXML
    private Button boton5;
     @FXML
    private Button boton6;
     @FXML
    private Button boton7;
     @FXML
    private Button boton8;
      @FXML
    private Button boton9;
    @FXML
    private Button boton0;
     @FXML
    private Button botSuma;
     @FXML
    private Button botRest;
    @FXML
    private Button botMult;
    @FXML
    private Button botDiv;
    @FXML
    private Button botonClear;
     @FXML
    private Button botonAdd;
    @FXML
    private Text textCounter;

    //Variables para controlarla
    private double primerNumero = 0;
    private String operador = "";
    private boolean nuevaOperacion = true;


    //Deteccion del numero pulsado y actualizacion del txt para mostrarlo por pantalla
     @FXML
    private void manejarNumero(ActionEvent event) {
        String valor = ((Button) event.getSource()).getText();
        String actual = textCounter.getText();

        if (textCounter.getText().equals("0") || nuevaOperacion) {
            textCounter.setText(valor);
            nuevaOperacion = false;
        } else {
            if (actual.length() < 10) { 
            textCounter.setText(actual + valor);
        }
    }
    }


    @FXML
    private void manejarOperacion(ActionEvent event) {
        Button boton = (Button) event.getSource();
        String actual = textCounter.getText();

        if (boton == botSuma) {
            operador = "+";
        } else if (boton == botRest) {
            operador = "-";
        } else if (boton == botMult) {
            operador = "*";
        } else if (boton == botDiv) {
            operador = "/";
        }
        //textCounter.setText(actual + operador);


        primerNumero = Double.parseDouble(textCounter.getText());
        nuevaOperacion = true;
    }
   

    @FXML
    private void calcularResultado(ActionEvent event) {
        double segundoNumero = Double.parseDouble(textCounter.getText());
        double resultado = 0;

        switch (operador) {
            case "+": resultado = primerNumero + segundoNumero; break;
            case "-": resultado = primerNumero - segundoNumero; break;
            case "*": resultado = primerNumero * segundoNumero; break;
            case "/":
                if (segundoNumero != 0) {
                    resultado = primerNumero / segundoNumero;
                } else {
                    textCounter.setText("Math Error");
                    return;
                }
                break;
        }
        // 🔹 Limitar a 10 caracteres máximo en pantalla
        String textoResultado = String.valueOf(resultado);

        // Ponemos de limite que s epuedn poner maximo 10 numeros
        if (textoResultado.length() > 10) {
            // Limitamos los decimales tambien
            textoResultado = String.format("%.6f", resultado);
            // Y si aun es largo, lo truncamos
            if (textoResultado.length() > 10) {
                textoResultado = textoResultado.substring(0, 10);
            }
        }

        textCounter.setText(textoResultado);
        nuevaOperacion = true;
    }

   @FXML
    private void borrar(ActionEvent event) {
        textCounter.setText("0");
        primerNumero = 0;
        operador = "";
        nuevaOperacion = true;
    }
}
