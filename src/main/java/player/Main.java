package player;

import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class Main extends Application {
    private final MusicPlayer player = new MusicPlayer();
    private Slider progressSlider;
    private boolean isSeeking = false;
    private AnimationTimer timer; // Timer do aktualizacji slidera
    @Override
    public void start(Stage primaryStage) {
    List<Song> mySongs = List.of(
    new Song("1", "Song One", "Artist A", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"),
    new Song("2", "Song Two", "Artist B", "https://www.bensound.com/bensound-music/bensound-summer.mp3")
    );
    Playlist playlist = new Playlist("Moja playlista", mySongs);
    // Zapisz do JSON
    PlaylistManager.savePlaylist(playlist, "playlist.json");
    // Wczytaj z JSON
    Playlist loaded = PlaylistManager.loadPlaylist("playlist.json");
    System.out.println("Loaded: " + loaded.name + ", songs: " + loaded.songs.size());

        int i[]={0};
        Button loadAndPlay = new Button("Play from URL");
        loadAndPlay.setOnAction(e -> {
            player.stop();//zatrzymanie muzyki
            player.load(loaded,i[0]);//zaladowanie muzyki
            player.setRate(1.0);//szybkosc odtwarzania
            player.play();//puszczenie muzyki
            startTimer();//rozpoczecie timera do progress baru
        });

        Button ContinueButton = new Button("Continue");
        Button Stop = new Button("Stop");
        Button Next = new Button("Next");

        progressSlider = new Slider(0, 100, 0);
        progressSlider.setMinWidth(250);

        // Obsługa zdarzeń suwaka (przesuwanie)
        progressSlider.setOnMousePressed(e -> isSeeking = true);

        progressSlider.setOnMouseReleased(e -> {
            isSeeking = false;
            double sliderValue = progressSlider.getValue();
            int seekPos = (int) ((sliderValue / 100) * player.getDuration());
            player.seekTo(seekPos);
        });

        Stop.setOnAction(e -> {
            player.pause();
        }
        );   
        ContinueButton.setOnAction(e -> {
            player.play();
        }
        ); 
        Next.setOnAction(e -> {
            player.stop();
            i[0]=(i[0]+1)%2;
            player.load(loaded,i[0]);
            player.play();
            startTimer();
        }
        );



        VBox root = new VBox(10, loadAndPlay, ContinueButton, Stop, Next, progressSlider);
        Scene scene = new Scene(root, 300, 160);

        primaryStage.setTitle("Music Player");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

   
        private void startTimer() {
            if (timer != null) {
                timer.stop();
            }
            timer = new AnimationTimer() {
                @Override
                public void handle(long now) { //handle to metoda do animowania to wykonuje sie co klatke 60kl/s
                    if (!isSeeking && player.isPlaying()) {
                        int currentPos = player.getCurrentPosition();
                        int duration = player.getDuration();
                        if (duration > 0) {
                            double progressPercent = (currentPos * 100.0) / duration;
                            progressSlider.setValue(progressPercent);
                        }
                    }
                }
            };
            timer.start();
        }
        

    public static void main(String[] args) {
        launch(args);
    }
}
