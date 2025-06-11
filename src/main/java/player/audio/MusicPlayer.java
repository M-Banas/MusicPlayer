package player.audio;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import player.model.Playlist;

public class MusicPlayer {

    private MediaPlayer mediaPlayer;

    public void load(Playlist play, int i) {
        if (play == null || play.getSongs() == null || play.getSongs().size() <= i) {
            System.out.println("Nie można załadować piosenki: brak utworów lub indeks poza zakresem");
            return;
        }

        Media media = new Media(play.getSongs().get(i).url);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnEndOfMedia(() -> {
            
        });
    }

    public void load(Playlist play, int i, Runnable onEnd) {
        if (play == null || play.getSongs() == null || play.getSongs().size() <= i) {
            System.out.println("Nie można załadować piosenki: brak utworów lub indeks poza zakresem");
            return;
        }

        Media media = new Media(play.getSongs().get(i).url);
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnEndOfMedia(onEnd);
    }

    // public void play() {
    //     if (mediaPlayer != null) {
    //         mediaPlayer.play();
    //     } else {
    //         System.out.println("mediaPlayer nie jest zainicjalizowany, nie można odtworzyć");
    //     }
    // }


    public void setOnEndOfMedia(Runnable r) {
        if (mediaPlayer != null) {
            mediaPlayer.setOnEndOfMedia(r);
        }
    }
    

    public void next(Playlist playlist, int nextIndex, Runnable onSongEnd) {
        stop();
        load(playlist, nextIndex);
        setRate(1.0);
    
        if (mediaPlayer != null) {
            mediaPlayer.setOnReady(() -> {
                mediaPlayer.setOnEndOfMedia(onSongEnd);
                mediaPlayer.play();
            });
        }
    }
    

    public void play() {
        if (mediaPlayer != null) {
            mediaPlayer.play();
            System.out.println("a");
        }
    }

    public void playOrResume(Playlist play, int i) {
        if (mediaPlayer == null) {
            load(play, i);
            mediaPlayer.play();
        } else {
            MediaPlayer.Status status = mediaPlayer.getStatus();
            if (status == MediaPlayer.Status.PAUSED || status == MediaPlayer.Status.STOPPED) {
                mediaPlayer.play();
            } else if (status == MediaPlayer.Status.READY) {
                mediaPlayer.play();
            } else {
                
            }
        }
    }
    
    public boolean isLoaded() {
        if (mediaPlayer == null) return false;
        MediaPlayer.Status status = mediaPlayer.getStatus();
        return status != MediaPlayer.Status.DISPOSED && status != MediaPlayer.Status.UNKNOWN;
    }
    

    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    public int getCurrentPosition() {
        if (mediaPlayer != null) {
            Duration currentTime = mediaPlayer.getCurrentTime();
            return (int) currentTime.toMillis();
        }
        return 0;
    }

    public int getDuration() {
        if (mediaPlayer != null) {
            Duration total = mediaPlayer.getTotalDuration();
            return total.isUnknown() ? 0 : (int) total.toMillis();
        }
        return 0;
    }

    public boolean isPlaying() {
        if (mediaPlayer != null) {
            return mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING;
        }
        return false;
    }

    public void seekTo(int millis) {
        if (mediaPlayer != null) {
            mediaPlayer.seek(Duration.millis(millis));
        }
    }

    public void setRate(double rate) {
        if (mediaPlayer != null) {
            // Ustawienie prędkości odtwarzania **po** załadowaniu medi (gdy MediaPlayer jest READY)
            mediaPlayer.setOnReady(() -> {
                mediaPlayer.setRate(rate);
            });
        }
    }

}
