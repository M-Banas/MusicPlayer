package player.model;
import java.util.List;

public class Playlist {
    private String name;
    private List<Song> songs;

    public Playlist(String name, List<Song> songs) {
        this.name = name;
        this.songs = songs;
    }

    public String getName() { return name; }
    public List<Song> getSongs() { return songs; }
    public void addSong(Song song) {
        if (!songs.contains(song)) {
            songs.add(song);
        }
    }
}
