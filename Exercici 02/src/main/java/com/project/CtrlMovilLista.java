package com.project;

import java.io.IOException;
import java.util.Map;

import com.project.model.Consola;
import com.project.model.Personaje;
import com.project.model.Videojuego;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public class CtrlMovilLista {

    // --- Elementos FXML ---
    @FXML private Node rootNode;
    @FXML private Label listTitle;
    @FXML private ListView<Object> itemListView;

    // --- Datos ---
    private DataManager dataManager;
    private String tipo;
    private Map<String, Personaje> personajes;
    private Map<String, Videojuego> videojuegos;
    private Map<String, Consola> consolas;

    /**
     * Configura la CellFactory al iniciar
     */
    @FXML
    public void initialize() {
        itemListView.setCellFactory(param -> new ItemCell());
    }

    /**
     * Recibe los datos y rellena la lista
     */
    public void initData(DataManager dataManager, String tipo) {
        this.dataManager = dataManager;
        this.tipo = tipo;
        this.listTitle.setText(tipo);

        this.personajes = dataManager.getPersonajes();
        this.videojuegos = dataManager.getVideojuegos();
        this.consolas = dataManager.getConsolas();

        // Rellena la lista con los objetos (.values())
        switch (tipo) {
            case "Personajes":
                itemListView.getItems().addAll(personajes.values());
                break;
            case "Videojuegos":
                itemListView.getItems().addAll(videojuegos.values());
                break;
            case "Consolas":
                itemListView.getItems().addAll(consolas.values());
                break;
        }
    }

    /**
     * Navega al detalle al hacer clic
     */
    @FXML
    private void onListItemClicked(MouseEvent event) {
        if (event.getClickCount() >= 1) { 
            Object itemSeleccionado = itemListView.getSelectionModel().getSelectedItem();
            if (itemSeleccionado == null) return;
            cargarVistaDetalle(itemSeleccionado);
        }
    }

    /**
     * Carga la vista de detalle
     */
    private void cargarVistaDetalle(Object item) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/assets/movilDetailView.fxml"));
            Parent nextRoot = loader.load();
            CtrlMovilDetalle detailController = loader.getController();
            detailController.initData(dataManager, item, this.tipo); 
            Scene scene = rootNode.getScene();
            scene.setRoot(nextRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Vuelve al menú principal
     */
    @FXML
    private void onVolverMenu() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/assets/movilMainView.fxml"));
            Parent prevRoot = loader.load();
            CtrlMovilMain mainController = loader.getController();
            mainController.setDataManager(dataManager);
            Scene scene = rootNode.getScene();
            scene.setRoot(prevRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Clase interna que define cómo se ve cada fila de la lista
     * (Versión actualizada con StackPane para alineación)
     */
    private class ItemCell extends ListCell<Object> {
        private HBox hbox = new HBox(10);
        private StackPane imageContainer = new StackPane();
        private ImageView imageView = new ImageView();
        private Label label = new Label();

        // Constructor de la celda
        public ItemCell() {
            super();
            
            imageView.setFitHeight(40);
            imageView.setFitWidth(40);
            imageView.setPreserveRatio(true);

            imageContainer.setMinWidth(50);
            imageContainer.setMaxWidth(50);
            imageContainer.getChildren().add(imageView);
            imageContainer.setAlignment(Pos.CENTER);

            label.setStyle("-fx-font-size: 16px;");
            hbox.setAlignment(Pos.CENTER_LEFT);
            hbox.getChildren().addAll(imageContainer, label); 
            hbox.setPadding(new Insets(8, 10, 8, 10));
        }

        /**
         * Este método se llama cada vez que hay que "dibujar" una celda
         * ¡¡AQUÍ ESTÁ EL CAMBIO PARA ARREGLAR EL BUG!!
         */
        @Override
        protected void updateItem(Object item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                // Si la celda está vacía, quitamos todo
                setText(null);
                setGraphic(null);
                
                // ¡¡AQUÍ!! Quitamos la clase del separador
                getStyleClass().remove("cell-separator");
                
            } else {
                // Si la celda está llena, ponemos todo
                
                // 1. Averiguamos qué es el ítem
                String nombre = "";
                String imagenFile = "";

                if (item instanceof Personaje) {
                    nombre = ((Personaje) item).getNombre();
                    imagenFile = ((Personaje) item).getImagen();
                } else if (item instanceof Videojuego) {
                    nombre = ((Videojuego) item).getNombre();
                    imagenFile = ((Videojuego) item).getImagen();
                } else if (item instanceof Consola) {
                    nombre = ((Consola) item).getNombre();
                    imagenFile = ((Consola) item).getImagen();
                }

                // 2. Ponemos los datos
                label.setText(nombre);
                try {
                    String rutaImg = "/assets/data/images/" + imagenFile;
                    Image img = new Image(getClass().getResourceAsStream(rutaImg));
                    imageView.setImage(img);
                } catch (Exception e) {
                    imageView.setImage(null); 
                }

                // 3. Mostramos el gráfico
                setGraphic(hbox);
                
                // ¡¡Y AQUÍ!! Añadimos la clase del separador
                getStyleClass().add("cell-separator");
            }
        }
    }
}