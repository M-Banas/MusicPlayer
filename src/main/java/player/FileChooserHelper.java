package player;

import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class FileChooserHelper {
    public static File chooseFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Wybierz plik MP3");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Pliki MP3", "*.mp3")
        );
        return fileChooser.showOpenDialog(new Stage());
    }
}
