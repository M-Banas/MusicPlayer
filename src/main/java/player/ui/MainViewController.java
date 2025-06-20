package player.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.controlsfx.control.CheckComboBox;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import player.audio.MusicPlayer;
import player.manager.PlaylistManager;
import player.model.Playlist;
import player.model.Song;

import player.model.SongRepository;

public class MainViewController {

    @FXML
    private Button playButton, pauseButton, stopButton, nextButton;
    @FXML
    private Slider progressSlider;
    @FXML
    private Label songTitleLabel;
    @FXML
    private javafx.scene.control.TextField searchField;
    @FXML
    private Button searchButton;
    @FXML
    private Button newPlaylistButton;
    @FXML
    private VBox searchResultsContainer;
    @FXML
    private VBox playlistContainer;
    @FXML
    private VBox songsContainer;
    @FXML
    private VBox playlistBox;
    @FXML
    private Button profileButton;
    @FXML
    private VBox centerVBox;
    @FXML
    private Node previousCenterContent;
    @FXML
    private VBox profileBox;
    @FXML
    private AnchorPane profileOverlay;
    @FXML
    private Label profileUsernameLabel;
    @FXML
    private ScrollPane searchResultsScrollPane;

    private List<Song> allSongs;
    private List<Playlist> playlists = new ArrayList<>();
    private Playlist currentPlaylist;
    private SongRepository songRepository = new SongRepository();
    private final MusicPlayer player = MusicPlayer.getInstance();
    private int currentIndex = 0;
    private boolean isSeeking = false;
    private AnimationTimer timer;
    private String userId;

    @FXML
    public void initialize() {
        userId = LoginController.userId;
        songRepository.fetchSongs();
        allSongs = songRepository.loadSongs();

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
        searchResultsContainer.setTranslateX(50);
        searchResultsContainer.setSpacing(10);

        progressSlider.setOnMousePressed(e -> isSeeking = true);
        progressSlider.setOnMouseReleased(e -> {
            isSeeking = false;
            int seekPos = (int) ((progressSlider.getValue() / 100) * player.getDuration());
            player.seekTo(seekPos);
        });

        // Obsługa końca utworu
        player.setRate(1.0);
        player.setOnEndOfMedia(() -> handleNext());

        // Obsługa wyszukiwania
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterSongs(newValue);
        });
        // Ukrywanie podpowiedzi po utracie fokusu
        searchField.focusedProperty().addListener((obs, wasFocused, isNowFocused) -> {
            if (!isNowFocused) {
                if (!isDescendantFocused(searchResultsContainer)) {
                    searchResultsContainer.setVisible(false);
                    searchResultsContainer.setManaged(false);
                    hideParentScrollPane(searchResultsContainer);
                }
            }
        });
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterSongs(newValue);
            boolean show = !newValue.isEmpty();
            searchResultsContainer.setVisible(show);
            searchResultsContainer.setManaged(show);
            showParentScrollPane(searchResultsContainer, show);
        });

        showHomeView();

        if (currentPlaylist != null && !currentPlaylist.getSongs().isEmpty()) {
            player.load(currentPlaylist, 0);
        }
        profileButton.setOnAction(e -> showProfile());

    }

    private void refreshPlaylists() {
        playlists.clear();
        playlistContainer.getChildren().clear();
        songsContainer.getChildren().clear();
        if (playlists != null) {
            for (var playlist : PlaylistManager.loadPlaylists(userId)) {
                playlists.add(playlist);
                addPlaylistToSidebar(playlist);
            }
        }
    }

    private void showHomeView() {
        playlistBox.setVisible(false);
        playlistBox.setManaged(false);

    }

    @FXML
    private void handlePlay() {
        if (currentPlaylist == null || currentPlaylist.getSongs().isEmpty()) {
            System.out.println("Brak piosenek w playliście!//handleplay");
            songTitleLabel.setText("Currently playing: -");
            return;
        }

        if (currentIndex < 0 || currentIndex >= currentPlaylist.getSongs().size()) {
            currentIndex = 0;
        }
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

    private boolean isShuffleActive;
    private Playlist shuffledPlaylist;
    
    @FXML
    private void handleNext() {
        if (isShuffleActive) {
            currentIndex = (currentIndex + 1) % shuffledPlaylist.getSongs().size();
            songTitleLabel.setText("Currently playing: " + shuffledPlaylist.getSongs().get(currentIndex).getTitle());
            player.next(shuffledPlaylist, currentIndex, this::handleNext);
        } else {
            currentIndex = (currentIndex + 1) % currentPlaylist.getSongs().size();
            songTitleLabel.setText("Currently playing: " + currentPlaylist.getSongs().get(currentIndex).getTitle());
            player.next(currentPlaylist, currentIndex, this::handleNext);
        }
        startTimer();
    }

    private void playSongFromPlaylist(Playlist playlist, int songIndex) {
        player.stop();
        player.load(playlist, songIndex);
        player.play();
        Song song = playlist.getSongs().get(songIndex);
        songTitleLabel.setText("Currently playing: " + song.getTitle());
        currentIndex = songIndex;
        currentPlaylist = playlist;
        startTimer();
    }

    private void generateShuffledPlaylist() {
        List<Song> songs = currentPlaylist.getSongs();
        List<Song> shuffledSongs = new ArrayList<>(songs);
        Collections.shuffle(shuffledSongs);
        shuffledPlaylist = new Playlist(currentPlaylist.getId(), currentPlaylist.getName(), shuffledSongs);
    }


    @FXML
    private void handleShuffleToggle() {
        isShuffleActive = !isShuffleActive;
        if (isShuffleActive) {
            generateShuffledPlaylist();
            currentIndex = 0;
            playSongFromPlaylist(shuffledPlaylist, currentIndex);
        } else {
            currentIndex = 0;
            playSongFromPlaylist(currentPlaylist, currentIndex);
        }
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
        dialog.setHeaderText(null);
        dialog.setContentText("enter playlist name:");

        var pane = dialog.getDialogPane();
        pane.setStyle(
                "-fx-background-color: #232323; -fx-border-color: #4db3cf; -fx-border-width: 2px; -fx-background-radius: 10; -fx-border-radius: 10;");
        pane.getStyleClass().add("white-prompt");
        pane.getStylesheets().add(getClass().getResource("/ui/style.css").toExternalForm());

        dialog.getDialogPane().getButtonTypes().forEach(type -> {
            Node btn = dialog.getDialogPane().lookupButton(type);
            if (btn != null) {
                if (type.getButtonData().isDefaultButton()) {
                    btn.setStyle("-fx-background-color: #4db3cf; -fx-text-fill: white; -fx-background-radius: 8;");
                } else {
                    btn.setStyle("-fx-background-color: #444; -fx-text-fill: #fff; -fx-background-radius: 8;");
                }
            }
        });

        dialog.showAndWait().ifPresent(name -> {
            String trimmedName = name.trim();
            if (!trimmedName.isEmpty()) {
                boolean exists = playlists.stream().anyMatch(pl -> pl.getName().equalsIgnoreCase(trimmedName));
                if (exists) {
                    Alert alert = new Alert(javafx.scene.control.Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("A playlist with this name already exists!");
                    alert.showAndWait();
                } else {
                    Playlist newPl = new Playlist(trimmedName, new ArrayList<>());
                    playlists.add(newPl);
                    PlaylistManager.createPlaylist(trimmedName, userId);
                    addPlaylistToSidebar(newPl);
                    refreshPlaylists();
                    filterSongs(searchField.getText());
                }
            }
        });
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().trim().toLowerCase();
        if (query.isEmpty())
            return;
        List<Song> found = new ArrayList<>();
        for (Song song : allSongs) {
            if (song.title.toLowerCase().contains(query) || song.artist.toLowerCase().contains(query)) {
                found.add(song);
            }
        }
        updateSearchResults(found);
    }

    private void addPlaylistToSidebar(Playlist p) {
        HBox row = new HBox(8);
        row.setAlignment(Pos.CENTER_LEFT);

        Button btn = new Button(p.getName());
        btn.setMaxWidth(Double.MAX_VALUE);
        if (currentPlaylist != null && p.getName().equals(currentPlaylist.getName())) {
            btn.setStyle("-fx-background-color: #4db3cf; -fx-text-fill: white; -fx-font-weight: bold;");
        } else {
            btn.setStyle("");
        }
        btn.setOnAction(e -> {
            currentPlaylist = p;
            showPlaylistSongs(p);
            for (Node node : playlistContainer.getChildren()) {
                if (node instanceof HBox hbox) {
                    for (Node child : hbox.getChildren()) {
                        if (child instanceof Button b && b.getText().equals(currentPlaylist.getName())) {
                            b.setStyle("-fx-background-color: #4db3cf; -fx-text-fill: white; -fx-font-weight: bold;");
                        } else if (child instanceof Button b2) {
                            b2.setStyle("");
                        }
                    }
                }
            }
        });

        Button deleteBtn = new Button("✖");
        deleteBtn.setStyle(
                "-fx-background-radius: 10; -fx-background-color: transparent; -fx-text-fill: #bbb; -fx-font-size: 13px; -fx-cursor: hand; -fx-padding: 0 6 0 6;");
        deleteBtn.setTooltip(new Tooltip("Usuń playlistę"));
        deleteBtn.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Playlist");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete the playlist '" + p.getName() + "'?");
            var pane = alert.getDialogPane();
            pane.setStyle(
                    "-fx-background-color: #232323; -fx-border-color: #cf4d4d; -fx-border-width: 2px; -fx-background-radius: 10; -fx-border-radius: 10;");
            pane.getStyleClass().add("white-prompt");
            pane.getStylesheets().add(getClass().getResource("/ui/style.css").toExternalForm());
            alert.getDialogPane().getButtonTypes().forEach(type -> {
                Node btnNode = alert.getDialogPane().lookupButton(type);
                if (btnNode != null) {
                    if (type.getButtonData().isDefaultButton()) {
                        btnNode.setStyle(
                                "-fx-background-color: #cf4d4d; -fx-text-fill: white; -fx-background-radius: 8;");
                    } else {
                        btnNode.setStyle("-fx-background-color: #444; -fx-text-fill: #fff; -fx-background-radius: 8;");
                    }
                }
            });
            alert.showAndWait().ifPresent(result -> {
                if (result == ButtonType.OK) {
                    PlaylistManager.removePlaylist(p.getId());
                    playlists.removeIf(pl -> pl.getId().equals(p.getId()));
                    refreshPlaylists();
                    if (currentPlaylist != null && currentPlaylist.getId().equals(p.getId())) {
                        currentPlaylist = null;
                        songsContainer.getChildren().clear();
                        songTitleLabel.setText("Currently playing: -");
                    }
                }
            });
        });

        HBox.setHgrow(btn, Priority.ALWAYS);
        row.getChildren().addAll(btn, deleteBtn);
        playlistContainer.getChildren().add(row);
    }

    private void showPlaylistSongs(Playlist playlist) {
        refreshPlaylists();
        currentPlaylist = playlist;
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
                //playSongFromPlaylist(playlist, songIndex);
                player.stop();
                player.load(playlist, songIndex);
                player.play();
                songTitleLabel.setText("Currently playing: " + song.getTitle());
                currentIndex = songIndex;
                currentPlaylist = playlist;
                startTimer();
            });
            Button deleteBtn = new Button("DELETE");
            deleteBtn.setStyle("-fx-background-radius: 5; -fx-background-color: #4db3cf; -fx-text-fill: white;");
            deleteBtn.setOnAction(e -> {
                PlaylistManager.removeSongFromPlaylist(playlist.getId(), song.getId());
                Playlist temp = PlaylistManager.getPlaylist(playlist.getId());
                showPlaylistSongs(temp);
            });
            Label songLabel = new Label(song.getTitle() + " - " + song.getArtist());
            songLabel.setStyle("-fx-text-fill: white;");
            songRow.getChildren().addAll(playBtn, songLabel, deleteBtn);
            songsContainer.getChildren().add(songRow);
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
            if (song.title.toLowerCase().contains(lowerCaseQuery)
                    || song.artist.toLowerCase().contains(lowerCaseQuery)) {
                HBox songBox = new HBox();
                songBox.setSpacing(10);
                songBox.setAlignment(Pos.CENTER_LEFT);
                songBox.setPadding(new Insets(5));
                songBox.setStyle("-fx-background-color: #2a2a2a; -fx-background-radius: 8;");

                Button playButton = new Button("▶");
                playButton.setStyle("-fx-background-color: #4db3cf; -fx-text-fill: white; -fx-background-radius: 5;");
                playButton.setOnAction(e -> {
                    player.stop();
                    Playlist containingPlaylist = currentPlaylist;
                    if (containingPlaylist == null || !containingPlaylist.getSongs().contains(song)) {
                        for (Playlist pl : playlists) {
                            if (pl.getSongs().contains(song)) {
                                containingPlaylist = pl;
                                break;
                            }
                        }
                    }
                    if (containingPlaylist != null) {
                        int idx = containingPlaylist.getSongs().indexOf(song);
                        currentPlaylist = containingPlaylist;
                        currentIndex = idx;
                        player.load(containingPlaylist, idx);
                        player.play();
                        songTitleLabel.setText("Currently playing: " + song.getTitle());
                    } else {
                        player.load(new Playlist("Single", List.of(song)), 0);
                        player.play();
                        songTitleLabel.setText("Currently playing: " + song.getTitle());
                        currentPlaylist = null;
                        currentIndex = 0;
                    }
                    startTimer();
                });

                Label songLabel = new Label(song.getTitle() + " - " + song.getArtist());
                songLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
                HBox.setHgrow(songLabel, Priority.ALWAYS);
                CheckComboBox<String> playlistCheckComboBox = new CheckComboBox<>();
                playlistCheckComboBox.getItems().addAll(getAllPlaylistNames());
                playlistCheckComboBox.setTitle("Dodaj do playlisty");

                Button addBtn = new Button("Dodaj");
                addBtn.setStyle("-fx-background-radius: 5; -fx-background-color: #4db34d; -fx-text-fill: white;");
                addBtn.setOnAction(ev -> {
                    for (String playlistName : playlistCheckComboBox.getCheckModel().getCheckedItems()) {
                        addSongToPlaylist(song, playlistName);
                    }
                });

                songBox.getChildren().addAll(playButton, songLabel, playlistCheckComboBox, addBtn);
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

            Button playButton = new Button("▶");
            playButton.setStyle("-fx-background-color: #4db3cf; -fx-text-fill: white; -fx-background-radius: 5;");
            playButton.setOnAction(e -> {
                player.stop();
                playSong(song);
            });
            Label songLabel = new Label(song.getTitle());
            songLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            HBox.setHgrow(songLabel, Priority.ALWAYS);
            CheckComboBox<String> playlistCheckComboBox = new CheckComboBox<>();
            playlistCheckComboBox.getItems().addAll(getAllPlaylistNames());
            playlistCheckComboBox.setTitle("Dodaj do playlisty");

            Button addBtn = new Button("Dodaj");
            addBtn.setStyle("-fx-background-radius: 5; -fx-background-color: #4db34d; -fx-text-fill: white;");
            addBtn.setOnAction(ev -> {
                for (String playlistName : playlistCheckComboBox.getCheckModel().getCheckedItems()) {
                    addSongToPlaylist(song, playlistName);
                }
            });

            songBox.getChildren().addAll(playButton, songLabel, playlistCheckComboBox, addBtn);
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
        songTitleLabel.setText("Currently playing: " + song.getTitle());
        int index = currentPlaylist.getSongs().indexOf(song);
        if (index >= 0) {
            currentIndex = index;
            player.playOrResume(currentPlaylist, currentIndex);
            startTimer();
        } else {
            System.out.println("Piosenka nie jest na aktualnej playliście");
        }
    }

    @FXML
    private void handleHome() {
        showHomeView();
        currentPlaylist = null;
    }

    private void showProfile() {
        profileOverlay.getChildren().clear();
        profileOverlay.setVisible(true);
        profileOverlay.setManaged(true);

        VBox sidebar = new VBox(30);
        sidebar.setStyle(
                "-fx-background-color: #1e1e1e; -fx-padding: 40 30 40 30; -fx-min-width: 350; -fx-max-width: 350;");
        sidebar.setAlignment(Pos.TOP_LEFT);

        Button backBtn = new Button();
        ImageView backArrow = new ImageView(new Image(getClass().getResourceAsStream("/ui/arrow-blue.png")));
        backArrow.setFitWidth(40);
        backArrow.setFitHeight(24);
        backArrow.setRotate(180);
        backBtn.setGraphic(backArrow);
        backBtn.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
        backBtn.setOnAction(e -> {
            profileOverlay.setVisible(false);
            profileOverlay.setManaged(false);
        });

        // Username
        Label userLabel = new Label("Username: " + getCurrentUsername());
        userLabel.setStyle("-fx-text-fill: #4db3cf; -fx-font-size: 22px;");

        // Liczba załadowanych playlist
        Label playlistCountLabel = new Label("Loaded playlists: " + playlists.size());
        playlistCountLabel.setStyle("-fx-text-fill: #4db3cf; -fx-font-size: 18px;");

        // Liczba wszystkich piosenek we wszystkich playlistach
        int totalSongs = playlists.stream().mapToInt(pl -> pl.getSongs().size()).sum();
        Label songsInPlaylistsLabel = new Label("Songs in playlists: " + totalSongs);
        songsInPlaylistsLabel.setStyle("-fx-text-fill: #4db3cf; -fx-font-size: 18px;");

        sidebar.getChildren().addAll(backBtn, userLabel, playlistCountLabel, songsInPlaylistsLabel);

        profileOverlay.getChildren().add(sidebar);
        AnchorPane.setRightAnchor(sidebar, 0.0);
        AnchorPane.setTopAnchor(sidebar, 65.0);
        AnchorPane.setBottomAnchor(sidebar, 0.0);
    }

    private String getCurrentUsername() {
        return LoginController.username != null ? LoginController.username : "Unknown";
    }

    // Pomocnicza metoda do ukrywania najbliższego nadrzędnego ScrollPane
    private void hideParentScrollPane(Node node) {
        Node parent = node.getParent();
        while (parent != null) {
            if (parent instanceof javafx.scene.control.ScrollPane scroll) {
                scroll.setVisible(false);
                scroll.setManaged(false);
                break;
            }
            parent = parent.getParent();
        }
    }

    // Pomocnicza metoda do pokazywania najbliższego nadrzędnego ScrollPane
    private void showParentScrollPane(Node node, boolean show) {
        Node parent = node.getParent();
        while (parent != null) {
            if (parent instanceof javafx.scene.control.ScrollPane scroll) {
                scroll.setVisible(show);
                scroll.setManaged(show);
                break;
            }
            parent = parent.getParent();
        }
    }

    private boolean isDescendantFocused(VBox container) {
        if (container.isFocused())
            return true;
        for (Node child : container.getChildren()) {
            if (child.isFocused())
                return true;
            if (child instanceof HBox hbox) {
                for (Node hChild : hbox.getChildren()) {
                    if (hChild.isFocused())
                        return true;
                }
            }
        }
        return false;
    }
}
