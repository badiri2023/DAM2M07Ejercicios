package com.project;

import java.io.IOException;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node; // Importante
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell; // Importante
import javafx.scene.control.ListView; // Importante
import javafx.scene.input.MouseEvent; // Importante

public class CtrlMovilMain {

    @FXML private Node rootNode; 

    // La ListView del menú
    @FXML private ListView<String> menuListView;

    private DataManager dataManager;

    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    /**
     * Se llama al iniciar la vista
     */
    @FXML
    public void initialize() {
        // 1. Rellenamos la lista con las 3 opciones
        menuListView.getItems().addAll("Personajes", "Videojuegos", "Consolas");

        // 2. Le decimos a la lista que use nuestra "MenuCell" personalizada
        menuListView.setCellFactory(param -> new MenuCell());
    }

    /**
     * Se llama al hacer clic en un ítem de la nueva lista
     */
    @FXML
    private void onMenuClicked(MouseEvent event) {
        String seleccion = menuListView.getSelectionModel().getSelectedItem();
        
        if (seleccion != null) {
            // Llamamos al método de navegación
            cargarVistaLista(seleccion);
        }
    }


    /**
     * El método MÁGICO para navegar
     */
    private void cargarVistaLista(String tipo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/assets/movilListView.fxml"));
            Parent nextRoot = loader.load();

            CtrlMovilLista listController = loader.getController();
            listController.initData(dataManager, tipo); 

            Scene scene = rootNode.getScene();
            scene.setRoot(nextRoot);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    
    /**
     * Clase interna para definir el estilo de la celda del MENÚ
     */
    private class MenuCell extends ListCell<String> {
        
        private Label label = new Label();

        public MenuCell() {
            super();
            
            // ¡¡CAMBIO AQUÍ!! (Texto más grande)
            label.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;"); // Antes: 16px
            
            // ¡¡CAMBIO AQUÍ!! (Más espacio arriba y abajo)
            setPadding(new Insets(40, 15, 40, 15));        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
                setGraphic(null);
                getStyleClass().remove("cell-separator");
            } else {
                label.setText(item);
                setGraphic(label);
                getStyleClass().add("cell-separator");
            }
        }
    }
}