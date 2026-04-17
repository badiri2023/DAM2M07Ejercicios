package com.project;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.geometry.Pos;
import javafx.application.Platform;
import javafx.scene.control.ScrollPane;

import java.io.File;
import java.nio.file.Files;

public class ChatFileHelper {

    private final VBox chatBox;
    private final ScrollPane chatScroll;

    public ChatFileHelper(VBox chatBox, ScrollPane chatScroll) {
        this.chatBox = chatBox;
        this.chatScroll = chatScroll;
    }

    public void addImage(File file, boolean isUser) {
        Image image = new Image(file.toURI().toString());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(200);
        imageView.setPreserveRatio(true);

        HBox container = new HBox(imageView);
        container.setSpacing(10);
        container.setAlignment(isUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

        chatBox.getChildren().add(container);

        Platform.runLater(() -> {
            chatScroll.layout();
            chatScroll.setVvalue(chatScroll.getVmax());
        });
    }

    public void addFile(File file, boolean isUser) {
        try {
            String content = Files.readString(file.toPath());
            Label label = new Label("Archivo: " + file.getName());
            HBox container = new HBox(label);
            container.setSpacing(10);
            container.setAlignment(isUser ? Pos.CENTER_RIGHT : Pos.CENTER_LEFT);

            chatBox.getChildren().add(container);

            Platform.runLater(() -> {
                chatScroll.layout();
                chatScroll.setVvalue(chatScroll.getVmax());
            });

            // Aquí podrías enviar el contenido a Ollama si quieres
        } catch (Exception e) {
            Label error = new Label("Error al leer archivo: " + e.getMessage());
            chatBox.getChildren().add(new HBox(error));
        }
    }
}
