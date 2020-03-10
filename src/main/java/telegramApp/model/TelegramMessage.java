package telegramApp.model;


import org.telegram.telegrambots.meta.api.objects.User;
import telegramApp.dto.TelegramUserDto;

import javax.persistence.*;

@Entity
public class TelegramMessage {

    @Id
    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "state_id")
    private int stateId;

    @Column(name = "performer_name")
    private String performerName;

    @Column(name = "song_name")
    private String songName;

    @Column(name = "song_id")
    private Long songId;

    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "position")
    private Long position;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride( name = "id", column = @Column(name = "t_user_id")),
            @AttributeOverride( name = "firstName", column = @Column(name = "t_user_first_name")),
            @AttributeOverride( name = "lastName", column = @Column(name = "t_user_last_name")),
            @AttributeOverride( name = "userName", column = @Column(name = "t_user_name")),
            @AttributeOverride( name = "languageCode", column = @Column(name = "t_user_language")),
            @AttributeOverride( name = "isBot", column = @Column(name = "is_t_user_bot"))
    })
    private TelegramUserDto telegramUserDto;

    /**
     * Для принятия решения о записи в базу данных на сервер pacman-player-core
     * факта посещения пользователем Telegram заведения (Company) нам нужно
     * знать, имеем ли мы дело с реальным посетителем заведения или человек
     * просто лазает в нашем боте и нажимает на кнопки. При срабатывании условий
     * нашей бизнес-логики это поле будет принимать значение true, если мы считаем,
     * что посетитель реальный.
     */
    @Column(name = "is_t_user_client")
    private boolean isTelegramUserOurClient;

    public TelegramMessage() {
    }

    public TelegramMessage(Long chatId, int stateId) {
        this.chatId = chatId;
        this.stateId = stateId;
    }

    public TelegramMessage(User user, int ordinal) {
        this.chatId = Long.valueOf(user.getId());
        this.stateId = ordinal;
        this.telegramUserDto = new TelegramUserDto(user);
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
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

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getPosition() {
        return position;
    }

    public void setPosition(Long position) {
        this.position = position;
    }

    public TelegramUserDto getTelegramUserDto() {
        return telegramUserDto;
    }

    public void setTelegramUserDto(TelegramUserDto telegramUserDto) {
        this.telegramUserDto = telegramUserDto;
    }

    public boolean isTelegramUserOurClient() {
        return isTelegramUserOurClient;
    }

    public void setTelegramUserOurClient(boolean telegramUserOurClient) {
        isTelegramUserOurClient = telegramUserOurClient;
    }
}
