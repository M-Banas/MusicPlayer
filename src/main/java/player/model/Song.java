package player.model;

public class Song {
    public String id;
    public String title;
    public String artist;
    public String url;

    public Song(String id, String title, String artist, String url) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.url = url;
    }
    
    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

}
