package player;
import java.util.List;

public class Playlist {
    public String name;
    public List<Song> songs;

    public Playlist(String name, List<Song> songs) {
        this.name = name;
        this.songs = songs;
    }
}
