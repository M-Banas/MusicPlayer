package player.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import player.audio.MusicPlayer;
import player.manager.PlaylistManager;
import player.model.Playlist;
import player.model.Song;

import player.model.SongRepository;

public class MainViewController {

    @FXML private Button playButton, pauseButton, stopButton, nextButton;
    @FXML private Slider progressSlider;
    @FXML private Label songTitleLabel;
    @FXML private javafx.scene.control.TextField searchField;
    @FXML private Button searchButton;
    @FXML private Button newPlaylistButton;
    @FXML private VBox searchResultsContainer; // Dodaj do pliku FXML <VBox fx:id="searchResultsContainer">
    @FXML private VBox playlistContainer;
    @FXML private VBox songsContainer; 
    @FXML private VBox playlistBox;

    private List<Song> allSongs; // wszystkie piosenki z repozytorium
    private List<Playlist> playlists = new ArrayList<>();
    private Playlist currentPlaylist;
    private SongRepository songRepository = new SongRepository();

    private final MusicPlayer player = new MusicPlayer();
    //private Playlist playlist;
    private int currentIndex = 0;
    private boolean isSeeking = false;
    private AnimationTimer timer;
    private int userId;
    //Obsługa panelu homeBox
    @FXML private VBox homeBox;
    @FXML private Label homeSongTitleLabel;
    @FXML private Button homePlayButton, homePauseButton, homeStopButton, homeNextButton, homeShuffleButton;
    @FXML private Slider homeProgressSlider;

    private List<Song> shuffledAllSongs = new ArrayList<>();
    private int homeCurrentIndex = 0;
    private boolean homeIsSeeking = false;
    private AnimationTimer homeTimer;
    
    @FXML
    public void initialize() {
        userId= LoginController.userId;
        songRepository.fetchSongs();
        allSongs = songRepository.loadSongs();

        // utwórz domyślną playlistę
        refreshPlaylists();
        playlistContainer.setPadding(new Insets(10));
        playlistContainer.setSpacing(10);
        playlistContainer.setStyle("-fx-background-color: #333333; -fx-border-color: #555555; -fx-border-width: 1px;");

        songsContainer.setPadding(new Insets(10));
        songsContainer.setSpacing(10);
        songsContainer.setStyle("-fx-background-color: #222222; -fx-border-color: #444444; -fx-border-width: 1px;");

        searchResultsContainer.prefWidthProperty().bind(searchField.widthProperty());
        searchResultsContainer.maxWidthProperty().bind(searchField.widthProperty());
        searchResultsContainer.setPadding(new Insets(10));
        searchResultsContainer.setSpacing(10);
        
        progressSlider.setOnMousePressed(e -> isSeeking = true);
        progressSlider.setOnMouseReleased(e -> {
            isSeeking = false;
            int seekPos = (int) ((progressSlider.getValue() / 100) * player.getDuration());
            player.seekTo(seekPos);
        });

        // Obsługa slidera postępu home
        setupHomeSlider();

        // Obsługa końca utworu
        player.setRate(1.0);
        player.setOnEndOfMedia(() -> handleNext());

        // Obsługa wyszukiwania
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterSongs(newValue);
        });

        // Pokaż panel home na start
        showHomeView();

        // Jeśli domyślna playlista ma piosenki, załaduj ją (opcjonalnie)
        if (currentPlaylist != null && !currentPlaylist.getSongs().isEmpty()) {
            player.load(currentPlaylist, 0);
        }
    }

    private void refreshPlaylists() {
        playlists.clear();
        playlistContainer.getChildren().clear();
        songsContainer.getChildren().clear();
        Playlist defaultPl = new Playlist("Ulubione", new ArrayList<>());
        playlists.add(defaultPl);
        currentPlaylist = defaultPl;
        addPlaylistToSidebar(defaultPl);

        if (playlists != null) {
            for (var playlist : PlaylistManager.loadPlaylists(userId)) {
                playlists.add(playlist);
                System.out.println("Loaded playlist: " + playlist.getName());
                addPlaylistToSidebar(playlist);               
            }
        }
    }

    private void showHomeView() {
        playlistBox.setVisible(false);
        playlistBox.setManaged(false);
        homeBox.setVisible(true);
        homeBox.setManaged(true);

        // Przygotuj losową kolejność wszystkich piosenek
        shuffledAllSongs = new ArrayList<>(allSongs);
        Collections.shuffle(shuffledAllSongs);
        homeCurrentIndex = 0;
        homeSongTitleLabel.setText("Currently playing: -"); 
        //updateHomeSongTitle();
        homeProgressSlider.setValue(0);
    }

    private void playSingleSong(Song song) {
        // Tworzymy tymczasową playlistę z jedną piosenką
        Playlist tempPlaylist = new Playlist("Losowa", List.of(song));
        player.load(tempPlaylist, 0);
        player.play();
    }

    @FXML
    private void handleHomePlay() {
        if (!shuffledAllSongs.isEmpty()) {
            Song song = shuffledAllSongs.get(homeCurrentIndex);
            playSingleSong(song);
            updateHomeSongTitle();
            startHomeTimer();
        }
    }

    @FXML
    private void handleHomePause() {
        player.pause();
    }   

    @FXML
    private void handleHomeStop() {
        player.stop();
        homeProgressSlider.setValue(0);
    }

    @FXML
    private void handleHomeNext() {
        if (!shuffledAllSongs.isEmpty()) {
            homeCurrentIndex = (homeCurrentIndex + 1) % shuffledAllSongs.size();
            player.stop();
            handleHomePlay();
        }
    }

    @FXML
    private void handleHomeShuffle() {
        Collections.shuffle(shuffledAllSongs);
        homeCurrentIndex = 0;
        handleHomePlay();
    }

    private void updateHomeSongTitle() {
        if (!shuffledAllSongs.isEmpty()) {
            homeSongTitleLabel.setText("Currently playing: " + shuffledAllSongs.get(homeCurrentIndex).getTitle());
        } else {
            homeSongTitleLabel.setText("Currently playing: -");
        }
    }

    private void setupHomeSlider() {
        homeProgressSlider.setOnMousePressed(e -> homeIsSeeking = true);
        homeProgressSlider.setOnMouseReleased(e -> {
            homeIsSeeking = false;
            int seekPos = (int) ((homeProgressSlider.getValue() / 100) * player.getDuration());
            player.seekTo(seekPos);
        });
    }

    private void startHomeTimer() {
        if (homeTimer != null) homeTimer.stop();
        homeTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!homeIsSeeking && player.isPlaying()) {
                    int currentPos = player.getCurrentPosition();
                    int duration = player.getDuration();
                    if (duration > 0) {
                        double percent = (currentPos * 100.0) / duration;
                        homeProgressSlider.setValue(percent);
                    }
                }
            }
        };
        homeTimer.start();
    }

    @FXML
    private void handlePlay() {
        songTitleLabel.setText("Currently playing: " + currentPlaylist.getSongs().get(currentIndex).title);
        player.playOrResume(currentPlaylist, currentIndex);
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
        currentIndex = (currentIndex + 1) % currentPlaylist.getSongs().size();
        songTitleLabel.setText("Currently playing: " + currentPlaylist.getSongs().get(currentIndex).title);
        player.next(currentPlaylist, currentIndex, this::handleNext);
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

    @FXML
    private void handleNewPlaylist() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Playlist");
        dialog.setHeaderText("Create a new playlist");
        dialog.setContentText("Enter playlist name:");

        dialog.showAndWait().ifPresent(name -> {
            if (!name.trim().isEmpty()) {
                Playlist newPl = new Playlist(name.trim(), new ArrayList<>());
                playlists.add(newPl);
                PlaylistManager.createPlaylist(name.trim(), userId);
                System.out.println("Created new playlist: " + name);
                addPlaylistToSidebar(newPl);
                refreshPlaylists();
            }
        });
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty()) return;

        List<Song> found = new ArrayList<>();
        for (Song song : allSongs) {
            if (song.title.toLowerCase().contains(query) || song.artist.toLowerCase().contains(query)) {
                found.add(song);
            }
        }
        updateSearchResults(found);
    }

    private void addPlaylistToSidebar(Playlist p) {
        Button btn = new Button(p.getName());
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setOnAction(e -> {
            currentPlaylist = p;
            showPlaylistSongs(p);
        });
        playlistContainer.getChildren().add(btn);
    }

    private void showPlaylistSongs(Playlist playlist) {
        homeBox.setVisible(false);
        homeBox.setManaged(false);
        playlistBox.setVisible(true);
        playlistBox.setManaged(true);
        songsContainer.getChildren().clear();
        List<Song> songs = playlist.getSongs();
        for (int i = 0; i < songs.size(); i++) {
            Song song = songs.get(i);
            int songIndex = i;

            HBox songRow = new HBox(10);
            songRow.setAlignment(Pos.CENTER_LEFT);

            Button playBtn = new Button("▶");
            playBtn.setStyle("-fx-background-radius: 5; -fx-background-color: #4db3cf; -fx-text-fill: white;");
            playBtn.setOnAction(e -> {
                player.load(playlist, songIndex);
                player.play();
                songTitleLabel.setText("Currently playing: " + song.getTitle());
                currentIndex = songIndex;
            });

            Label songLabel = new Label(song.getTitle() + " - " + song.getArtist());
            songLabel.setStyle("-fx-text-fill: white;");

            songRow.getChildren().addAll(playBtn, songLabel);
            songsContainer.getChildren().add(songRow);
        }
    }

    @FXML
    private void handleShuffle() {
        if (currentPlaylist != null && !currentPlaylist.getSongs().isEmpty()) {
            List<Song> shuffled = new ArrayList<>(currentPlaylist.getSongs());
            Collections.shuffle(shuffled);
            player.load(new Playlist(currentPlaylist.getName(), shuffled), 0);
            player.play();
            songTitleLabel.setText("Currently playing: " + shuffled.get(0).getTitle());
            currentIndex = 0;
        }
    }

    private void filterSongs(String query) {
        String lowerCaseQuery = query.toLowerCase();
        searchResultsContainer.getChildren().clear();

        if (lowerCaseQuery.isEmpty()) {
            searchResultsContainer.setVisible(false);
            return;
        }
        searchResultsContainer.setVisible(true);

        for (Song song : allSongs) {
            if (song.title.toLowerCase().contains(lowerCaseQuery) || song.artist.toLowerCase().contains(lowerCaseQuery)) {
                HBox songBox = new HBox();
                songBox.setSpacing(10);
                songBox.setAlignment(Pos.CENTER_LEFT);
                songBox.setPadding(new Insets(5));
                songBox.setStyle("-fx-background-color: #2a2a2a; -fx-background-radius: 8;");

                Button playButton = new Button("🔊");
                playButton.setStyle("-fx-background-color: #4db3cf; -fx-text-fill: white; -fx-background-radius: 5;");
                playButton.setOnAction(e -> playSong(song));

                Label songLabel = new Label(song.getTitle() + " - " + song.getArtist());
                songLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
                HBox.setHgrow(songLabel, Priority.ALWAYS);

                ComboBox<String> playlistComboBox = new ComboBox<>();
                playlistComboBox.getItems().addAll(getAllPlaylistNames());
                playlistComboBox.setPromptText("➕ Do playlisty");
                playlistComboBox.setStyle("-fx-background-radius: 5; -fx-background-color: #4db34d; -fx-text-fill: white;");
                playlistComboBox.setOnAction(e -> {
                String selectedPlaylist = playlistComboBox.getValue();
                if (selectedPlaylist != null) {
                    addSongToPlaylist(song, selectedPlaylist);
                    }
                });

                songBox.getChildren().addAll(playButton, songLabel, playlistComboBox);
                searchResultsContainer.getChildren().add(songBox);
            }
        }
    }

    private void updateSearchResults(List<Song> foundSongs) {
        searchResultsContainer.getChildren().clear();

        for (Song song : foundSongs) {
            HBox songBox = new HBox();
            songBox.setSpacing(10);
            songBox.setAlignment(Pos.CENTER_LEFT);
            songBox.setPadding(new Insets(5));
            songBox.setStyle("-fx-background-color: #2a2a2a; -fx-background-radius: 8;");

            // Przycisk odsłuchania
            Button playButton = new Button("🔊");
            playButton.setStyle("-fx-background-color: #4db3cf; -fx-text-fill: white; -fx-background-radius: 5;");
            playButton.setOnAction(e -> playSong(song));

            // Etykieta z tytułem piosenki
            Label songLabel = new Label(song.getTitle());
            songLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            HBox.setHgrow(songLabel, Priority.ALWAYS);

            // ComboBox z listą playlist
            ComboBox<String> playlistComboBox = new ComboBox<>();
            playlistComboBox.getItems().addAll(getAllPlaylistNames()); // zakładamy że masz taką metodę
            playlistComboBox.setPromptText("➕ Do playlisty");
            playlistComboBox.setStyle("-fx-background-radius: 5; -fx-background-color: #4db34d; -fx-text-fill: white;");

            playlistComboBox.setOnAction(e -> {
                String selectedPlaylist = playlistComboBox.getValue();
                if (selectedPlaylist != null) {
                    addSongToPlaylist(song, selectedPlaylist);
                }
            });

            songBox.getChildren().addAll(playButton, songLabel, playlistComboBox);
            searchResultsContainer.getChildren().add(songBox);
        }
    }

    private List<String> getAllPlaylistNames() {
        return playlists.stream().map(Playlist::getName).collect(Collectors.toList());
    }

    private void addSongToPlaylist(Song song, String playlistName) {
        for (Playlist pl : playlists) {
            if (pl.getName().equals(playlistName)) {
               if (!pl.getSongs().contains(song)) {
                    PlaylistManager.addSongToPlaylist(pl.getId(), song.getId());
                    pl.getSongs().add(song);
                }
                break;
            }
        }
    }

    private void playSong(Song song) {
        System.out.println("Odtwarzanie piosenki: " + song.getTitle());
        songTitleLabel.setText("Currently playing: " + song.getTitle());
        // Tutaj podłącz odtwarzanie z MusicPlayer, np.:
        int index = currentPlaylist.getSongs().indexOf(song);
        if (index >= 0) {
            currentIndex = index;
            player.playOrResume(currentPlaylist, currentIndex);
            startTimer();
        } else {
        // Jeśli piosenka nie jest w aktualnej playliście, można ją tam dodać lub odtworzyć inaczej
            System.out.println("Piosenka nie jest na aktualnej playliście");
        }
    }

    @FXML
    private void handleHome() {
        showHomeView();
        songTitleLabel.setText("Currently playing: -");
        currentPlaylist = null;
    }

    private void showPlaylistChoiceDialog(String songName) {
        List<String> playlistNames = playlists.stream()
                                              .map(Playlist::getName)
                                              .collect(Collectors.toList());

        ChoiceDialog<String> dialog = new ChoiceDialog<>(playlistNames.isEmpty() ? null : playlistNames.get(0), playlistNames);
        dialog.setTitle("Wybierz playlistę");
        dialog.setHeaderText("Dodaj \"" + songName + "\" do playlisty");
        dialog.setContentText("Wybierz playlistę:");

        dialog.showAndWait().ifPresent(selectedPlaylistName -> {
            for (Song song : allSongs) {
                if (song.getTitle().equals(songName)) {
                    addSongToPlaylist(song, selectedPlaylistName);
                    break;
                }
            }
        });
    }

}
