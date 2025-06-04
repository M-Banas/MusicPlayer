package player.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
public void start(Stage primaryStage) throws Exception {

    FXMLLoader loginLoader = new FXMLLoader(getClass().getResource("/ui/LoginView.fxml"));
    Parent loginRoot = loginLoader.load();

   
    Scene loginScene = new Scene(loginRoot);
    loginScene.getStylesheets().add(getClass().getResource("/ui/style.css").toExternalForm());

    // Logowanie
    Stage loginStage = new Stage();
    loginStage.setTitle("Login");
    loginStage.setScene(loginScene);
    loginStage.showAndWait(); 

    // MainView
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
