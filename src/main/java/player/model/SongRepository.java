package player.model;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SongRepository {
    private List<Song> songs;

    public  SongRepository() {
        songs = new ArrayList<>();
    }

    public List<Song> getSongs() {
        if (songs.isEmpty()) {
            fetchSongs();
        }
        return songs;
    }

    public  void fetchSongs() {
        try {
            URL url = new URL("http://localhost:8080/songs");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            Gson gson = new Gson();
            songs = gson.fromJson(reader, new TypeToken<List<Song>>(){}.getType());
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<Song> loadSongs() {
        fetchSongs();
        return songs;
    }
    
}