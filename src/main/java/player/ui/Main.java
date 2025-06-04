package player.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
public void start(Stage primaryStage) throws Exception {
    // 1. Ładujemy FXML logowania
    FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/ui/LoginView.fxml"));
    Parent loginRoot = loginLoader.load();

    // 2. Tworzymy scenę logowania + dodajemy CSS
    Scene loginScene = new Scene(loginRoot);
    loginScene.getStylesheets().add(getClass().getResource("/ui/style.css").toExternalForm());

    // 3. Pokazujemy okno logowania
    Stage loginStage = new Stage();
    loginStage.setTitle("Login");
    loginStage.setScene(loginScene);
    loginStage.showAndWait();  // blokuje do momentu zamknięcia

    // 4. Po logowaniu pokazujemy główną scenę
    FXMLLoader mainLoader = new FXMLLoader(getClass().getResource("/ui/MainView.fxml"));
    Parent mainRoot = mainLoader.load();

    Scene mainScene = new Scene(mainRoot, 400, 250);
    mainScene.getStylesheets().add(getClass().getResource("/ui/style.css").toExternalForm());

    primaryStage.setTitle("Music Player");
    primaryStage.setScene(mainScene);
    primaryStage.show();
}


    public static void main(String[] args) {
        launch(args);
    }
}
