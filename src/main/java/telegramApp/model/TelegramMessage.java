package telegramApp.model;


import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.File;

@Entity
public class TelegramMessage {

    @Id
    private Long chatId;
    private int stateId;
    private String performerName;
    private String songName;
    private Long songId;

    public TelegramMessage() {
    }

    public TelegramMessage(Long chatId, int stateId) {
        this.chatId = chatId;
        this.stateId = stateId;
    }

    public int getStateId() {
        return stateId;
    }

    public void setStateId(int stateId) {
        this.stateId = stateId;
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


}
