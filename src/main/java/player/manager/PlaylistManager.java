package player.manager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import player.model.Playlist;
import player.model.Song;

public class PlaylistManager {

    public static List<Playlist> loadPlaylists(int id) {
        List<Playlist> playlists = null;
        try {
            URL url = new URL("http://localhost:8080/person/" + id + "/playlists");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            Gson gson = new Gson();
            playlists = gson.fromJson(reader, new TypeToken<List<Playlist>>(){}.getType());
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playlists;
    }

    // Dodawanie piosenki do playlisty przez endpoint
    public static boolean addSongToPlaylist(int playlistId, String songId) {
        try {
            URL url = new URL("http://localhost:8080/playlist/" + playlistId + "/addSong?songId=" + songId);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            int responseCode = connection.getResponseCode();
            return responseCode == 200 || responseCode == 201;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Usuwanie piosenki z playlisty przez endpoint 
    public static boolean removeSongFromPlaylist(int playlistId, String songId) {
        try {
            URL url = new URL("http://localhost:8080/playlist/" + playlistId + "/removeSong?songId=" + songId);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            int responseCode = connection.getResponseCode();
            return responseCode == 200 || responseCode == 201 || responseCode == 204;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    // Tworzenie nowej playlisty przez endpoint
    public static boolean createPlaylist(String name, int ownerId) {
        try {
            String urlStr = String.format("http://localhost:8080/playlist/create?name=%s&ownerId=%d",
                    java.net.URLEncoder.encode(name, "UTF-8"), ownerId);
            URL url = new URL(urlStr);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            int responseCode = connection.getResponseCode();
            return responseCode == 200 || responseCode == 201;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}