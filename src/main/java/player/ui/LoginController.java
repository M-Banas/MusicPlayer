package player.ui;

import java.net.HttpURLConnection;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import player.model.Playlist;
import player.util.HttpUtil;

public class LoginController {
    public static int userId;
    public static boolean isLoggedIn = false;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void initialize() {
        usernameField.setPrefWidth(300);
        passwordField.setPrefWidth(300);
    }

    @FXML
    private void handleLogin() {
        String user = usernameField.getText();
        String pass = passwordField.getText();
        String response=null;
        try {
            HttpURLConnection connection = HttpUtil.getGetConnection("/person/login?username=" + HttpUtil.encodeParam(user) + "&password=" + HttpUtil.encodeParam(pass));
            response = HttpUtil.readResponse(connection);
            System.out.println("Response: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(response == null || response.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd logowania");
            alert.setHeaderText("Nie można połączyć się z serwerem");
            alert.setContentText("Sprawdź połączenie internetowe lub spróbuj ponownie później.");

            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(
                    getClass().getResource("/ui/style.css").toExternalForm());
            dialogPane.getStyleClass().add("custom-alert");

            alert.showAndWait();
            return;
        }
        else {
            userId = Integer.parseInt(response);
            System.out.println("Zalogowano jako użytkownik o ID: " + userId);
            isLoggedIn = true;
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.close();
        }
    }
}