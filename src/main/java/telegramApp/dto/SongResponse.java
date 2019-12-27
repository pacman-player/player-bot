package telegramApp.dto;

public class SongResponse {

    private Long chatId;
    private Long songId;
    private byte[] track;
    private String trackName;

    public SongResponse() {
    }

    public SongResponse(Long chatId, Long songId, byte[] track, String trackName) {
        this.chatId = chatId;
        this.songId = songId;
        this.track = track;
        this.trackName = trackName;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getSongId() {
        return songId;
    }

    public void setSongId(Long songId) {
        this.songId = songId;
    }

    public byte[] getTrack() {
        return track;
    }

    public void setTrack(byte[] track) {
        this.track = track;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }
}
