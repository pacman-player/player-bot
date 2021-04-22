package telegramApp.dto;

public class BotSongDto {

    private Long songId;
    private String trackName;
    private boolean banned;

    public BotSongDto() {
    }

    public BotSongDto(Long songId, String trackName) {
        this.songId = songId;
        this.trackName = trackName;
    }

    public BotSongDto(Long songId, String trackName, boolean banned) {
        this.songId = songId;
        this.trackName = trackName;
        this.banned = banned;
    }

    public Long getSongId() {
        return songId;
    }

    public void setSongId(Long songId) {
        this.songId = songId;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }
}
