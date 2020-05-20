package telegramApp.dto;

import java.util.List;

public class SongsListResponse {

    private List<BotSongDto> songs;

    public SongsListResponse() {
    }

    public SongsListResponse(List<BotSongDto> songs) {
        this.songs = songs;
    }

    public List<BotSongDto> getSongs() {
        return songs;
    }

    public void setSongs(List<BotSongDto> songs) {
        this.songs = songs;
    }
}
