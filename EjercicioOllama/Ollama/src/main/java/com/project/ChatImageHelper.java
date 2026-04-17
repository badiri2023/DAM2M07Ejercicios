package com.project;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.application.Platform;

import java.io.File;

public class ChatImageHelper {

    private final VBox chatBox;

    public ChatImageHelper(VBox chatBox) {
        this.chatBox = chatBox;
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
            chatBox.layout();
            if (chatBox.getParent() instanceof javafx.scene.control.ScrollPane scroll) {
                scroll.setVvalue(scroll.getVmax());
            }
        });
    }
}
