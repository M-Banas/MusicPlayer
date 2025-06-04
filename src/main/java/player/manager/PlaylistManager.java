package player.manager;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import player.model.Playlist;

public class PlaylistManager {
    private static final Gson gson = new Gson();

    public static void savePlaylist(Playlist playlist, String filename) {
        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(playlist, writer);
            System.out.println("Playlist saved to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Playlist loadPlaylist(String filename) {
        try (FileReader reader = new FileReader(filename)) {
            Type type = new TypeToken<Playlist>() {}.getType();
            return gson.fromJson(reader, type);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
