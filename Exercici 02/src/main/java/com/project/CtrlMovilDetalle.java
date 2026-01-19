package com.project;

import java.io.IOException;

import com.project.model.Consola;
import com.project.model.Personaje;
import com.project.model.Videojuego;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label; 
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color; 
import javafx.scene.shape.Rectangle;

public class CtrlMovilDetalle {

    // --- Elementos FXML ---
    @FXML private Node rootNode;
    @FXML private Label detailTitle;
    @FXML private ImageView itemImage;
    @FXML private Label itemName;
    
    // Asegúrate de que estos dos coinciden con el FXML
    @FXML private Label itemDescription; 
    @FXML private Rectangle itemColorRect; 
    
    // --- Datos ---
    private DataManager dataManager;
    private String tipoListaAnterior;

    /**
     * Método personalizado para recibir los datos desde CtrlMovilLista
     */
    public void initData(DataManager dataManager, Object item, String tipoListaAnterior) {
        this.dataManager = dataManager;
        this.tipoListaAnterior = tipoListaAnterior;
        mostrarDetalle(item);
    }

    /**
     * Rellena los campos de la vista con el objeto recibido
     */
    private void mostrarDetalle(Object item) {
        
        itemColorRect.setVisible(false);

        if (item instanceof Personaje) {
            Personaje p = (Personaje) item;
            detailTitle.setText(p.getNombre());
            itemName.setText(p.getNombre());
            
            // Ponemos el color en el rectángulo y lo hacemos visible
            try {
                itemColorRect.setFill(Color.web(p.getColor()));
                itemColorRect.setVisible(true);
            } catch (Exception e) {
                itemColorRect.setVisible(false);
            }
            
            // info del Label
            itemDescription.setText("Juego: " + p.getJuego());
            cargarImagen(p.getImagen());
            
        } else if (item instanceof Videojuego) {
            Videojuego v = (Videojuego) item;
            detailTitle.setText(v.getNombre());
            itemName.setText(v.getNombre());
                        
            // info del Label
            itemDescription.setText("Año: " + v.getAno() + "\nTipo: " + v.getTipo() + "\n\n" + v.getDescripcion());
            cargarImagen(v.getImagen());
            
        } else if (item instanceof Consola) {
            Consola c = (Consola) item;
            detailTitle.setText(c.getNombre());
            itemName.setText(c.getNombre());
            
            // Ponemos el color en el rectángulo y lo hacemos visible
            try {
                itemColorRect.setFill(Color.web(c.getColor()));
                itemColorRect.setVisible(true);
            } catch (Exception e) {
                itemColorRect.setVisible(false);
            }

            // info del Label
            itemDescription.setText("Fecha: " + c.getFecha() + "\nProcesador: " + c.getProcesador() +
                    "\nUnidades Vendidas: " + c.getUnidadesVendidas());
            cargarImagen(c.getImagen());
        }
    }

    /**
     * Carga la imagen desde resources
     */
    private void cargarImagen(String nombreArchivo) {
        try {
            String rutaCompleta = "/assets/data/images/" + nombreArchivo;
            Image img = new Image(getClass().getResourceAsStream(rutaCompleta));
            itemImage.setImage(img);
        } catch (Exception e) {
            itemImage.setImage(null);
            System.err.println("No se pudo encontrar la imagen en: " + "/assets/data/images/" + nombreArchivo);
        }
    }

    /**
     * Se llama al pulsar el botón "Volver a la Lista"
     */
    @FXML
    private void onVolverLista() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/assets/movilListView.fxml"));
            Parent prevRoot = loader.load();
            CtrlMovilLista listController = loader.getController();
            listController.initData(dataManager, tipoListaAnterior); 
            Scene scene = rootNode.getScene();
            scene.setRoot(prevRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
