package player.manager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import player.model.Playlist;
import player.util.HttpUtil;
import java.net.HttpURLConnection;
import java.util.List;

public class PlaylistManager {

    public static List<Playlist> loadPlaylists(int id) {
        try {
            HttpURLConnection connection = HttpUtil.getGetConnection("/person/" + id + "/playlists");
            String response = HttpUtil.readResponse(connection);
            return new Gson().fromJson(response, new TypeToken<List<Playlist>>(){}.getType());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean addSongToPlaylist(String playlistId, String songId) {
        try {
            String endpoint = String.format("/playlist/%s/addSong?songId=%s", 
                playlistId, HttpUtil.encodeParam(songId));
            HttpURLConnection connection = HttpUtil.getPostConnection(endpoint);
            return HttpUtil.isSuccessful(connection.getResponseCode());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean removeSongFromPlaylist(int playlistId, String songId) {
        try {
            String endpoint = String.format("/playlist/%d/removeSong?songId=%s", 
                playlistId, HttpUtil.encodeParam(songId));
            HttpURLConnection connection = HttpUtil.getPostConnection(endpoint);
            return HttpUtil.isSuccessful(connection.getResponseCode());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean createPlaylist(String name, int ownerId) {
        try {
            String endpoint = String.format("/playlist/create?name=%s&ownerId=%d",
                HttpUtil.encodeParam(name), ownerId);
            HttpURLConnection connection = HttpUtil.getPostConnection(endpoint);
            return HttpUtil.isSuccessful(connection.getResponseCode());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}