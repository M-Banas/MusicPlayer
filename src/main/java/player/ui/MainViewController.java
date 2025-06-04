package player.ui;

import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import player.audio.MusicPlayer;
import player.manager.PlaylistManager;
import player.model.Playlist;
import player.model.Song;

public class MainViewController {

    @FXML private Button playButton, pauseButton, stopButton, nextButton;
    @FXML private Slider progressSlider;
    @FXML private Label songTitleLabel;

    private final MusicPlayer player = new MusicPlayer();
    private Playlist playlist;
    private int currentIndex = 0;
    private boolean isSeeking = false;
    private AnimationTimer timer;
    
    @FXML
    public void initialize() {
        List<Song> songs = List.of(
                new Song("1", "Song One", "Artist A", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"),
                new Song("2", "Song Two", "Artist B", "https://www.bensound.com/bensound-music/bensound-summer.mp3"),
                new Song("3","Song 3","Artist C","https://www.bensound.com/bensound-music/bensound-sunny.mp3")
        );
        playlist = new Playlist("Example Playlist", songs);
        PlaylistManager.savePlaylist(playlist, "playlist.json");
        progressSlider.setOnMousePressed(e -> isSeeking = true);
        progressSlider.setOnMouseReleased(e -> {
            isSeeking = false;
            int seekPos = (int) ((progressSlider.getValue() / 100) * player.getDuration());
            player.seekTo(seekPos);
        });
        player.load(playlist, currentIndex);
        player.setRate(1.0);
        player.setOnEndOfMedia(() -> handleNext());
    }

    
    @FXML
    private void handlePlay() {
        songTitleLabel.setText("Currently playing: " + playlist.songs.get(currentIndex).title);
        player.playOrResume(playlist, currentIndex);
        startTimer();
    }

    @FXML
    private void handlePause() {
        player.pause();
    }

    @FXML
    private void handleStop() {
        player.stop();
    }

    @FXML
    private void handleNext() {
    currentIndex = (currentIndex + 1) % playlist.songs.size();
    songTitleLabel.setText("Currently playing: " + playlist.songs.get(currentIndex).title);
    player.next(playlist, currentIndex, this::handleNext);
    startTimer();
    }




    private void startTimer() {
        if (timer != null) {
            timer.stop();
        }

        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!isSeeking && player.isPlaying()) {
                    int currentPos = player.getCurrentPosition();
                    int duration = player.getDuration();
                    if (duration > 0) {
                        double percent = (currentPos * 100.0) / duration;
                        progressSlider.setValue(percent);
                    }
                }
            }
        };
        timer.start();
    }
}
