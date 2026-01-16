package com.project;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONArray;
import org.json.JSONObject;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class Controller {
    // ollama
    private static final String TEXT_MODEL   = "gemma3:1b";
    // imagenes llava
    private static final String VISION_MODEL = "llava-phi3:latest";

    @FXML private ScrollPane scrollPane;
    @FXML private VBox chatBox;
    @FXML private TextField inputPrompt;
    @FXML private Label labelArchivo;
    @FXML private Button btnSend;
    @FXML private Button btnFile;
    @FXML private Button btnCancel;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private final AtomicBoolean isCancelled = new AtomicBoolean(false);
    
    private InputStream currentInputStream;
    private JSONArray contextHistory = new JSONArray(); 
    private File pendingFile = null;

    // VARIABLE CLAVE: Para saber en qué burbuja está escribiendo la IA ahora mismo
    private Label currentAiLabel;

    @FXML
    public void initialize() {
        // Auto-scroll y mensaje de bienbenida
        chatBox.heightProperty().addListener((obs, oldVal, newVal) -> scrollPane.setVvalue(1.0));
        addMessage("Hola soy Ollama la Llama, en que puedo ayudarte?", false);
    }

    // --- diseño bafaradas ---
    // los prompts y respuestas las motramos dependiendo de si es usuario o Ollama, donde luego lso mostramos
    private void addMessage(String text, boolean isUser) {
        Label label = new Label(text);
        label.setWrapText(true);
        label.setMaxWidth(380);  

        if (isUser) {
            label.getStyleClass().add("bubble-user");
        } else {
            label.getStyleClass().add("bubble-ai");
            currentAiLabel = label;
        }
        HBox container = new HBox(label);
        
        if (isUser) {
            container.setAlignment(Pos.CENTER_RIGHT); 
            container.setPadding(new javafx.geometry.Insets(0, 0, 0, 50)); 
        } else {
            container.setAlignment(Pos.CENTER_LEFT);
            container.setPadding(new javafx.geometry.Insets(0, 50, 0, 0)); 
        }
        Platform.runLater(() -> chatBox.getChildren().add(container));
    }

    private void appendToAiBubble(String chunk) {
        Platform.runLater(() -> {
            if (currentAiLabel != null) {
                currentAiLabel.setText(currentAiLabel.getText() + chunk);
            }
        });
    }

    // --- LOGICA DE ENVÍO ---

    private void processFileRequest(String userPrompt) throws IOException {
        String fileName = pendingFile.getName().toLowerCase();
        if (userPrompt.isEmpty()) userPrompt = "Describe esto.";

        if (fileName.matches(".*\\.(jpg|png|jpeg|webp)")) {
            byte[] bytes = Files.readAllBytes(pendingFile.toPath());
            String base64 = Base64.getEncoder().encodeToString(bytes);
            contextHistory = new JSONArray(); 
            executeImageRequest(userPrompt, base64);
        } else {
            String content = Files.readString(pendingFile.toPath());
            String fullPrompt = "Archivo: " + fileName + "\nContenido:\n" + content + "\n\nInstrucción: " + userPrompt;
            executeTextRequest(fullPrompt);
        }
    }

    // leemos la respuesta y la introucirmos a la bafarada
    private void readStream(InputStream is) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (isCancelled.get()) break;
                JSONObject json = new JSONObject(line);
                
                if (json.has("response")) {
                    appendToAiBubble(json.getString("response"));
                }
                if (json.optBoolean("done", false)) {
                    if (json.has("context")) contextHistory = json.getJSONArray("context");
                    toggleUI(false);
                }
            }
        } catch (Exception e) {}
    }
    

    // Drag & Drop de las imagenes
    @FXML private void handleDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) event.acceptTransferModes(TransferMode.COPY);
        event.consume();
    }
    @FXML private void handleDragDropped(DragEvent event) {
        var db = event.getDragboard();
        if (db.hasFiles()) {
            this.pendingFile = db.getFiles().get(0);
            labelArchivo.setText("📎 " + pendingFile.getName());
            labelArchivo.setVisible(true);
            labelArchivo.setStyle("-fx-text-fill: #a6e3a1;");
            event.setDropCompleted(true);
        }
        event.consume();
    }
    // --botones--
   @FXML
    private void handleSend(ActionEvent event) {
        String prompt = inputPrompt.getText().trim();
        if (prompt.isEmpty() && pendingFile == null) return;

        inputPrompt.clear();
        isCancelled.set(false);
        toggleUI(true); 

        if (pendingFile != null) {
            addMessage("Imagen: [" + pendingFile.getName() + "]\n" + prompt, true);
        } else {
            addMessage(prompt, true);
        }

        addMessage("", false);

        if (pendingFile != null) {
            try { processFileRequest(prompt); } 
            catch (Exception e) { appendToAiBubble("Error leyendo archivo."); toggleUI(false); }
            pendingFile = null;
            labelArchivo.setVisible(false);
        } else {
            executeTextRequest(prompt);
        }
    }

    @FXML private void handleCancel(ActionEvent event) {
        isCancelled.set(true);
        try { if(currentInputStream != null) currentInputStream.close(); } catch(Exception e){}
        appendToAiBubble(" [DETENIDO]");
        toggleUI(false);
    }
    @FXML private void handleSelectFile(ActionEvent event) {
        FileChooser fc = new FileChooser();
        File f = fc.showOpenDialog(btnSend.getScene().getWindow());
        if(f!=null) {
            pendingFile=f;
            labelArchivo.setText("📎 "+f.getName());
            labelArchivo.setVisible(true);
            labelArchivo.setStyle("-fx-text-fill: #a6e3a1;");
        }
    }

    private void toggleUI(boolean disable) {
        Platform.runLater(() -> {
            btnSend.setDisable(disable);
            btnFile.setDisable(disable);
        });
    }

    // --conexion emisora--
    // conexion a Ollama
    private HttpRequest buildRequest(JSONObject body) {
        return HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:11434/api/generate"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();
    }
    // funcion para enviar el json
    private void executeTextRequest(String prompt) {
        JSONObject body = new JSONObject();
        body.put("model", TEXT_MODEL);
        body.put("prompt", prompt);
        body.put("stream", true);
        if (!contextHistory.isEmpty()) body.put("context", contextHistory);

        HttpRequest request = buildRequest(body);

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
            .thenAccept(resp -> {
                if (resp.statusCode() == 200) {
                    currentInputStream = resp.body();
                    executorService.submit(() -> readStream(currentInputStream));
                } else {
                    handleError(resp.statusCode());
                }
            })
            .exceptionally(e -> { handleError(-1); return null; });
    }
    // json en caso de que se envie imagen
    private void executeImageRequest(String prompt, String base64) {
        JSONObject body = new JSONObject();
        body.put("model", VISION_MODEL);
        body.put("prompt", prompt);
        body.put("stream", false);
        body.put("images", new JSONArray().put(base64));

        HttpRequest request = buildRequest(body);
        appendToAiBubble("Analizando imagen... "); 

        httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenAccept(resp -> {
                String responseText = new JSONObject(resp.body()).optString("response", "");
                Platform.runLater(() -> currentAiLabel.setText(responseText));
                toggleUI(false);
            })
            .exceptionally(e -> { handleError(-1); return null; });
    }

    // --errores--
    
    private void handleError(int code) {
        Platform.runLater(() -> {
            appendToAiBubble("❌ Error: " + code);
            toggleUI(false);
        });
    }
}
