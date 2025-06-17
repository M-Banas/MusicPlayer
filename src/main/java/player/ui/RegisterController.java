package player.ui;

import java.net.HttpURLConnection;
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

public class RegisterController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private void handleRegister() {
        if (!validateFields()) {
            return;
        }

        try {
            String username = usernameField.getText();
            String password = passwordField.getText();

            HttpURLConnection connection = HttpUtil.getPostConnection("/person/add?username=" + 
                HttpUtil.encodeParam(username) + 
                "&password=" + HttpUtil.encodeParam(password));

            int responseCode = connection.getResponseCode();
            
            if (HttpUtil.isSuccessful(responseCode)) {
                showAlert(Alert.AlertType.INFORMATION, "Sukces", "Rejestracja udana", "Możesz się teraz zalogować.");
                handleBackToLogin();
            } else {
                showAlert(Alert.AlertType.ERROR, "Błąd", "Rejestracja nieudana", "Spróbuj ponownie później.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd", "Błąd połączenia", "Sprawdź połączenie internetowe.");
        }
    }

    private boolean validateFields() {
        if (usernameField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nieprawidłowe dane", "Nazwa użytkownika nie może być pusta.");
            return false;
        }

        if (passwordField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nieprawidłowe dane", "Hasło nie może być puste.");
            return false;
        }

        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            showAlert(Alert.AlertType.ERROR, "Błąd", "Nieprawidłowe dane", "Hasła muszą być takie same.");
            return false;
        }

        return true;
    }

    @FXML
    private void handleBackToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui/LoginView.fxml"));
            Parent loginRoot = loader.load();
            Scene loginScene = new Scene(loginRoot);
            loginScene.getStylesheets().add(getClass().getResource("/ui/style.css").toExternalForm());

            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(loginScene);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Błąd", "Błąd aplikacji", "Nie można wrócić do ekranu logowania.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/ui/style.css").toExternalForm());
        dialogPane.getStyleClass().add("custom-alert");

        alert.showAndWait();
    }
}
