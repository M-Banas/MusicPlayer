package player.ui;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import player.audio.MusicPlayer;
import player.manager.PlaylistManager;
import player.model.Playlist;
import player.model.SongRepository;

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
        SongRepository songsRepository = new SongRepository();
        System.out.println("Available songs: " + songsRepository.getSongs().size());
        playlist = PlaylistManager.loadPlaylist("playlist.json");
        progressSlider.setOnMousePressed(e -> isSeeking = true);
        progressSlider.setOnMouseReleased(e -> {
            isSeeking = false;
            int seekPos = (int) ((progressSlider.getValue() / 100) * player.getDuration());
            player.seekTo(seekPos);
        });
        player.load(playlist, currentIndex);
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

    @FXML private VBox playlistContainer;
    @FXML private Button newPlaylistButton;

    @FXML
    private void handleNewPlaylist() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Playlist");
        dialog.setHeaderText("Create a new playlist");
        dialog.setContentText("Enter playlist name:");

        dialog.showAndWait().ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                addPlaylistToSidebar(name.trim());
            }
        });
    }

    private void addPlaylistToSidebar(String name) {
        HBox playlistBox = new HBox(10);
        playlistBox.setStyle(
            "-fx-background-color: #121212;" +   
            "-fx-padding: 8 10 8 10;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;"
        );
        playlistBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        playlistBox.setMinHeight(30);

        Label icon = new Label("🎵");
        icon.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");

        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        playlistBox.getChildren().addAll(icon, nameLabel);

        playlistBox.setOnMouseEntered(e -> playlistBox.setStyle(
            "-fx-background-color: #555555; " + 
            "-fx-padding: 8 10 8 10; " +
            "-fx-border-radius: 6; " +
            "-fx-background-radius: 6;"));
        playlistBox.setOnMouseExited(e -> playlistBox.setStyle(
            "-fx-background-color: #333333;" +
            "-fx-padding: 8 10 8 10;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;"));

        playlistContainer.getChildren().add(playlistBox);
    }



}
