package com.project;

import java.util.Map;

import com.project.model.Consola;
import com.project.model.Personaje;
import com.project.model.Videojuego;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class CtrlPc {

    @FXML private ComboBox<String> categorySelector;
    @FXML private ListView<String> listItems;
    @FXML private ImageView itemImage;
    @FXML private Label itemName;
    @FXML private Label itemDescription;
    @FXML private VBox contentPane;

    private Map<String, Personaje> personajes;
    private Map<String, Videojuego> videojuegos;
    private Map<String, Consola> consolas;

    private DataManager dataManager;
    private String categoriaActual;

    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
        this.personajes = dataManager.getPersonajes();
        this.videojuegos = dataManager.getVideojuegos();
        this.consolas = dataManager.getConsolas();

        // Cargar categorías
        categorySelector.getItems().addAll("Personajes", "Videojuegos", "Consolas");

        // Selección por defecto: Videojuegos
        categorySelector.getSelectionModel().select("Videojuegos");
        categoriaActual = "Videojuegos";
        cargarLista("Videojuegos");
    }

    @FXML
    public void initialize() {
        // Ajustes del ImageView de detalle
        itemImage.setFitHeight(240);
        itemImage.setFitWidth(240);
        itemImage.setPreserveRatio(true);

        // Cambio de categoría
        categorySelector.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                categoriaActual = newVal;
                cargarLista(newVal);
            }
        });

        // Selección de item para mostrar detalle
        listItems.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                mostrarDetalle(newVal);
            }
        });

        // CellFactory: miniatura + nombre con separador morado y estilo de selección morado oscuro
        listItems.setCellFactory(lv -> new ListCell<String>() {
            private final ImageView icon = new ImageView();
            private final Label text = new Label();
            private final HBox content = new HBox(10, icon, text);

            {
                icon.setFitHeight(30);
                icon.setFitWidth(30);
                icon.setPreserveRatio(true);
                content.setStyle("-fx-padding: 6 8 6 8; -fx-alignment: CENTER_LEFT;");
                // Listener para actualizar estilo cuando cambia la selección
                selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
                    applySelectionStyle(isNowSelected);
                });
            }

            @Override
            protected void updateItem(String nombre, boolean empty) {
                super.updateItem(nombre, empty);
                if (empty || nombre == null) {
                    setGraphic(null);
                    setText(null);
                    setStyle("");
                } else {
                    // Cargar imagen pequeña según categoría actual
                    String archivo = null;
                    if ("Personajes".equals(categoriaActual) && personajes != null && personajes.containsKey(nombre)) {
                        archivo = personajes.get(nombre).getImagen();
                    } else if ("Videojuegos".equals(categoriaActual) && videojuegos != null && videojuegos.containsKey(nombre)) {
                        archivo = videojuegos.get(nombre).getImagen();
                    } else if ("Consolas".equals(categoriaActual) && consolas != null && consolas.containsKey(nombre)) {
                        archivo = consolas.get(nombre).getImagen();
                    }

                    if (archivo != null) {
                        try {
                            Image img = new Image(getClass().getResourceAsStream("/assets/data/images/" + archivo));
                            icon.setImage(img);
                        } catch (Exception e) {
                            icon.setImage(null);
                        }
                    } else {
                        icon.setImage(null);
                    }

                    text.setText(nombre);
                    setGraphic(content);

                    // Aplicar estilo según estado de selección actual
                    applySelectionStyle(isSelected());
                }
            }

            private void applySelectionStyle(boolean selected) {
                if (selected) {
                    // Borde morado oscuro y fondo sutil cuando la celda está seleccionada
                    setStyle("-fx-background-color: rgba(106,0,184,0.06); -fx-border-color: #6a00b8; -fx-border-width: 0 0 3 0;");
                    text.setStyle("-fx-font-weight: bold; -fx-text-fill: -fx-text-base-color;");
                } else {
                    // separador morado claro en la parte inferior
                    setStyle("-fx-background-color: transparent; -fx-border-color: #ff00ee; -fx-border-width: 0 0 2 0;");
                    text.setStyle("-fx-font-weight: normal; -fx-text-fill: -fx-text-base-color;");
                }
            }
        });
    }

    private void cargarLista(String categoria) {
        listItems.getItems().clear();
        if (categoria == null) return;

        switch (categoria) {
            case "Personajes":
                if (personajes != null) listItems.getItems().addAll(personajes.keySet());
                break;
            case "Videojuegos":
                if (videojuegos != null) listItems.getItems().addAll(videojuegos.keySet());
                break;
            case "Consolas":
                if (consolas != null) listItems.getItems().addAll(consolas.keySet());
                break;
        }
        limpiarDetalle();
    }

    private void mostrarDetalle(String nombre) {
        limpiarDetalle();
        if (nombre == null) return;

        if (personajes != null && personajes.containsKey(nombre)) {
            Personaje p = personajes.get(nombre);
            itemName.setText(p.getNombre());
            itemDescription.setText("Color: " + p.getColor() + "\nJuego: " + p.getJuego());
            cargarImagen(p.getImagen());

        } else if (videojuegos != null && videojuegos.containsKey(nombre)) {
            Videojuego v = videojuegos.get(nombre);
            itemName.setText(v.getNombre());
            itemDescription.setText("Año: " + v.getAno() + "\nTipo: " + v.getTipo() + "\n\n" + v.getDescripcion());
            cargarImagen(v.getImagen());

        } else if (consolas != null && consolas.containsKey(nombre)) {
            Consola c = consolas.get(nombre);
            itemName.setText(c.getNombre());
            itemDescription.setText("Fecha: " + c.getFecha() + "\nProcesador: " + c.getProcesador() +
                    "\nColor: " + c.getColor() + "\nUnidades Vendidas: " + c.getUnidadesVendidas());
            cargarImagen(c.getImagen());
        }
    }

    private void cargarImagen(String nombreArchivo) {
        try {
            if (nombreArchivo == null || nombreArchivo.isEmpty()) {
                itemImage.setImage(null);
                return;
            }
            String rutaCompleta = "/assets/data/images/" + nombreArchivo;
            Image img = new Image(getClass().getResourceAsStream(rutaCompleta));
            itemImage.setImage(img);
        } catch (Exception e) {
            itemImage.setImage(null);
            System.err.println("No se pudo encontrar la imagen en: " + "/assets/data/images/" + nombreArchivo);
        }
    }

    private void limpiarDetalle() {
        itemImage.setImage(null);
        itemName.setText("");
        itemDescription.setText("");
    }
}
