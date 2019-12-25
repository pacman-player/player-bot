package telegramApp.model;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.File;

@Entity
public class TelegramMessage {

    @Id
    private Long chatId;
    private int statetId;
    private String performerName;
    private String songName;
    private Long songId;
    @Transient
    private File track;

    public TelegramMessage() {
    }

    public TelegramMessage(Long chatId, int statetId) {
        this.chatId = chatId;
        this.statetId = statetId;
    }

    public int getStatetId() {
        return statetId;
    }

    public void setStatetId(int statetId) {
        this.statetId = statetId;
    }

    public String getPerformerName() {
        return performerName;
    }

    public void setPerformerName(String performerName) {
        this.performerName = performerName;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public Long getSongId() {
        return songId;
    }

    public void setSongId(Long songId) {
        this.songId = songId;
    }

    public File getTrack() {
        return track;
    }

    public void setTrack(File track) {
        this.track = track;
    }

    @Override
    public String toString() {
        return "TelegramMessage{" +
                "chatId=" + chatId +
                ", statetId=" + statetId +
                ", performerName='" + performerName + '\'' +
                ", songName='" + songName + '\'' +
                ", songId=" + songId +
                '}';
    }
}
