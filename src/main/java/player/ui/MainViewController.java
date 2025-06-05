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
            new Song("1", "Inspiring Ambient", "Lexin_Music", "https://cdn.pixabay.com/audio/2023/03/30/audio_1a020fd142.mp3"),
            new Song("2", "Tech House Vibe", "FASSounds", "https://cdn.pixabay.com/audio/2022/10/25/audio_865b3b86e1.mp3"),
            new Song("3", "Deep Ambient", "Dream-Protocol", "https://cdn.pixabay.com/audio/2022/03/10/audio_60dc92be63.mp3"),
            new Song("4", "Calm Meditation", "Bensound", "https://www.bensound.com/bensound-music/bensound-slowmotion.mp3"),
            new Song("5", "Funky Element", "Bensound", "https://www.bensound.com/bensound-music/bensound-funkyelement.mp3"),
            new Song("6", "A Day to Remember", "Lesfm", "https://cdn.pixabay.com/audio/2022/10/30/audio_f6b196e1a4.mp3"),
            new Song("7", "Epic Cinematic", "AudioCoffee", "https://cdn.pixabay.com/audio/2022/10/13/audio_fcc3f5d5e0.mp3"),
            new Song("8", "Chill Abstract", "Coma-Media", "https://cdn.pixabay.com/audio/2022/03/24/audio_2e9fcd1b2a.mp3"),
            new Song("9", "Piano Emotion", "FASSounds", "https://cdn.pixabay.com/audio/2023/03/03/audio_22f08b8a18.mp3"),
            new Song("10", "Tropical Dance", "Musictown", "https://cdn.pixabay.com/audio/2023/04/23/audio_b1c1aeb89c.mp3"),
            new Song("11", "Electronic Vibes", "WILDBIRD", "https://cdn.pixabay.com/audio/2023/04/07/audio_4dfc0b785b.mp3"),
            new Song("12", "Smooth Jazz", "Coffee_Music", "https://cdn.pixabay.com/audio/2022/03/15/audio_98379805c5.mp3"),
            new Song("13", "Light Piano", "Pikasonic", "https://cdn.pixabay.com/audio/2022/02/24/audio_878ddef700.mp3"),
            new Song("14", "Corporate Motivational", "FreeGroove", "https://cdn.pixabay.com/audio/2022/08/24/audio_b62c38ef99.mp3"),
            new Song("15", "Lo-Fi Vibes", "AlexiAction", "https://cdn.pixabay.com/audio/2022/09/18/audio_2633bb98fd.mp3"),
            new Song("16", "Minimal Background", "Lesfm", "https://cdn.pixabay.com/audio/2022/03/08/audio_58a4f9a17d.mp3"),
            new Song("17", "Slow Cinematic", "Bensound", "https://www.bensound.com/bensound-music/bensound-sadness.mp3"),
            new Song("18", "Atmospheric Lounge", "LiteSaturation", "https://cdn.pixabay.com/audio/2022/12/14/audio_6e83114c27.mp3"),
            new Song("19", "Electronic Chill", "FASSounds", "https://cdn.pixabay.com/audio/2023/03/14/audio_b9366fd0d2.mp3"),
            new Song("20", "Pop Uplifting", "FASSounds", "https://cdn.pixabay.com/audio/2023/03/28/audio_8ec12b67db.mp3"),
            new Song("21", "Hip Hop Beat", "Coma-Media", "https://cdn.pixabay.com/audio/2022/03/28/audio_e1d57b0625.mp3"),
            new Song("22", "Acoustic Happy", "Bensound", "https://www.bensound.com/bensound-music/bensound-happyrock.mp3"),
            new Song("23", "Relaxing Guitar", "Pixabay", "https://cdn.pixabay.com/audio/2022/04/04/audio_424e7d91ed.mp3"),
            new Song("24", "Modern Chill", "Lesfm", "https://cdn.pixabay.com/audio/2022/03/10/audio_d33a4408aa.mp3"),
            new Song("25", "Urban Hip Hop", "Coffee_Music", "https://cdn.pixabay.com/audio/2023/03/04/audio_2e42888ab5.mp3"),
            new Song("26", "Smooth Piano", "Dream-Protocol", "https://cdn.pixabay.com/audio/2022/05/04/audio_265308f5b4.mp3"),
            new Song("27", "Hopeful Background", "FASSounds", "https://cdn.pixabay.com/audio/2023/03/30/audio_f32aa2cf98.mp3"),
            new Song("28", "Upbeat Folk", "Bensound", "https://www.bensound.com/bensound-music/bensound-buddy.mp3"),
            new Song("29", "Calm Lo-Fi", "Lesfm", "https://cdn.pixabay.com/audio/2022/05/14/audio_4b74874510.mp3"),
            new Song("30", "Inspiring Strings", "FASSounds", "https://cdn.pixabay.com/audio/2023/03/25/audio_5c2f9f8409.mp3"),
            new Song("31", "Song One", "Artist A", "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"),
            new Song("32", "Song Two", "Artist B", "https://www.bensound.com/bensound-music/bensound-summer.mp3"),
            new Song("33","Song 3","Artist C","https://www.bensound.com/bensound-music/bensound-sunny.mp3")
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
