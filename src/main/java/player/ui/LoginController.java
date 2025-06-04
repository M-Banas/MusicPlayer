package player.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleLogin() {
        String user = usernameField.getText();
        String pass = passwordField.getText();

        if ("user".equals(user) && "pass".equals(pass)) {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.close();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Błąd logowania");
            alert.setHeaderText("Niepoprawne dane logowania");
            alert.setContentText("Spróbuj ponownie.");

            // ✨ DODAJ styl z style.css
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStylesheets().add(
                getClass().getResource("/ui/style.css").toExternalForm()
            );
            dialogPane.getStyleClass().add("custom-alert");

            alert.showAndWait();
        }
    }



}
