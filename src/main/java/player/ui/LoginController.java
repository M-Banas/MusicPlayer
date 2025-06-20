package player.ui;

import java.net.HttpURLConnection;
//import java.util.List;

//import com.google.gson.Gson;
//import com.google.gson.JsonObject;
//import com.google.gson.reflect.TypeToken;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import player.util.HttpUtil;

public class LoginController {
    public static String userId;
    public static String username;
    public static String password;
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
        String response = null;
        try {
            HttpURLConnection connection = HttpUtil.getGetConnection("/person/login?username=" + HttpUtil.encodeParam(user) + "&password=" + HttpUtil.encodeParam(pass));
            response = HttpUtil.readResponse(connection);
            System.out.println("Response: " + response);
        } catch (Exception e) {
            e.printStackTrace();
            // Błąd połączenia z serwerem
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd logowania");
            alert.setHeaderText("Nie można połączyć się z serwerem");
            alert.setContentText("Sprawdź połączenie internetowe lub spróbuj ponownie później.");

            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(getClass().getResource("/ui/style.css").toExternalForm());
            dialogPane.getStyleClass().add("custom-alert");

            alert.showAndWait();
            return;
        }
        if (response == null || response.isEmpty() || response.equals("null")) {
            // Błędne dane logowania
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd logowania");
            alert.setHeaderText("Nieprawidłowa nazwa użytkownika lub hasło");
            alert.setContentText("Spróbuj ponownie. Jeśli nie masz konta, zarejestruj się.");

            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(getClass().getResource("/ui/style.css").toExternalForm());
            dialogPane.getStyleClass().add("custom-alert");

            alert.showAndWait();
            return;
        } else {
            userId = response;
            username = user;
            password = pass;
            System.out.println("Zalogowano jako użytkownik o ID: " + userId);
            isLoggedIn = true;
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    private void handleRegister() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/RegisterView.fxml"));
            Parent registerRoot = loader.load();
            Scene registerScene = new Scene(registerRoot);
            registerScene.getStylesheets().add(getClass().getResource("/ui/style.css").toExternalForm());

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(registerScene);
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd");
            alert.setHeaderText("Nie można otworzyć formularza rejestracji");
            alert.setContentText("Spróbuj ponownie później.");

            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(getClass().getResource("/ui/style.css").toExternalForm());
            dialogPane.getStyleClass().add("custom-alert");

            alert.showAndWait();
        }
    }
}