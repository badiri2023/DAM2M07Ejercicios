package com.project;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.event.ActionEvent;
import javafx.application.Platform;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

import org.json.JSONObject;

public class ControllerDesktop {

    @FXML private VBox chatBox;
    @FXML private TextField inputPrompt;
    @FXML private ScrollPane chatScroll;

    private final HttpClient client = HttpClient.newHttpClient();
    private CompletableFuture<HttpResponse<String>> currentRequest;
    private ChatFileHelper fileHelper;

    @FXML
    private void initialize() {
        inputPrompt.setOnAction(event -> handleSendText(null));

        // Listener que baja el scroll siempre que el chat crece
        chatBox.heightProperty().addListener((obs, oldVal, newVal) -> {
            chatScroll.setVvalue(chatScroll.getVmax());
        });

        fileHelper = new ChatFileHelper(chatBox, chatScroll);
    }

    private void addMessage(String text, boolean isUser) {
        Label message = new Label(text);
        message.setWrapText(true);
        message.setStyle("-fx-background-color: #3b0a68; -fx-text-fill: white; -fx-padding: 8; -fx-background-radius: 8;");

        HBox container = new HBox(message);
        container.setSpacing(10);

        if (isUser) {
            container.setAlignment(Pos.CENTER_RIGHT);
        } else {
            container.setAlignment(Pos.CENTER_LEFT);
        }

        chatBox.getChildren().add(container);
    }

    @FXML
    private void handleSendText(ActionEvent event) {
        try {
            String prompt = inputPrompt.getText().trim();
            if (prompt.isEmpty()) return;

            addMessage("Tú: " + prompt, true);

            String body = "{ \"model\": \"gemma3:1b\", \"prompt\": \"" + prompt + "\", \"stream\": false }";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://localhost:11434/api/generate"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            currentRequest = client.sendAsync(request, HttpResponse.BodyHandlers.ofString());

            currentRequest.thenAccept(response -> {
                JSONObject json = new JSONObject(response.body());
                String ollamaReply = json.getString("response");

                Platform.runLater(() -> {
                    addMessage("Ollama: " + ollamaReply, false);
                    chatScroll.layout();
                    chatScroll.setVvalue(chatScroll.getVmax());
                });
            });

            inputPrompt.clear();
        } catch (Exception e) {
            addMessage("Error al conectar con Ollama: " + e.getMessage(), false);
        }
    }

    @FXML
    private void handleSendFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecciona un archivo");
        File file = fileChooser.showOpenDialog(chatBox.getScene().getWindow());

        if (file != null) {
            String name = file.getName().toLowerCase();
            if (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".jpeg")) {
                fileHelper.addImage(file, true);
            } else {
                fileHelper.addFile(file, true);
            }
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        if (currentRequest != null && !currentRequest.isDone()) {
            currentRequest.cancel(true);
            addMessage("Respuesta cancelada.", false);
        } else {
            addMessage("No hay petición en curso para cancelar.", false);
        }
    }
}
