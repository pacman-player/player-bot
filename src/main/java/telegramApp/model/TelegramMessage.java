package telegramApp.model;


import org.telegram.telegrambots.meta.api.objects.User;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class TelegramMessage {

    @Id
    private Long chatId;
    private String telegramUserFirstName;
    private Boolean isTelegramUserBot;
    private String telegramUserLastName;
    private String telegramUserName;
    private String telegramUserLanguageCode;
    private int stateId;
    private String performerName;
    private String songName;
    private Long songId;
    private Long companyId;
    private Long position;
    /**
     * Для принятия решения о записи в базу данных на сервер pacman-player-core
     * факта посещения пользователем Telegram заведения (Company) нам нужно
     * знать, имеем ли мы дело с реальным посетителем заведения или человек
     * просто лазает в нашем боте и нажимает на кнопки. При срабатывании условий
     * нашей бизнес-логики это поле будет принимать значение true, если мы считаем,
     * что посетитель реальный.
     */
    private boolean isTelegramUserOurClient;

    public TelegramMessage() {
    }

    public TelegramMessage(Long chatId, int stateId) {
        this.chatId = chatId;
        this.stateId = stateId;
    }

    public TelegramMessage(User user, int ordinal) {
        this.chatId = Long.valueOf(user.getId());
        this.telegramUserFirstName = user.getFirstName();
        this.isTelegramUserBot = user.getBot();
        this.telegramUserLastName = user.getLastName();
        this.telegramUserName = user.getUserName();
        this.telegramUserLanguageCode = user.getLanguageCode();
        this.stateId = ordinal;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getTelegramUserFirstName() {
        return telegramUserFirstName;
    }

    public void setTelegramUserFirstName(String telegramUserFirstName) {
        this.telegramUserFirstName = telegramUserFirstName;
    }

    public Boolean getTelegramUserBot() {
        return isTelegramUserBot;
    }

    public void setTelegramUserBot(Boolean telegramUserBot) {
        isTelegramUserBot = telegramUserBot;
    }

    public String getTelegramUserLastName() {
        return telegramUserLastName;
    }

    public void setTelegramUserLastName(String telegramUserLastName) {
        this.telegramUserLastName = telegramUserLastName;
    }

    public String getTelegramUserName() {
        return telegramUserName;
    }

    public void setTelegramUserName(String telegramUserName) {
        this.telegramUserName = telegramUserName;
    }

    public String getTelegramUserLanguageCode() {
        return telegramUserLanguageCode;
    }

    public void setTelegramUserLanguageCode(String telegramUserLanguageCode) {
        this.telegramUserLanguageCode = telegramUserLanguageCode;
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

    public boolean isTelegramUserOurClient() {
        return isTelegramUserOurClient;
    }

    public void setTelegramUserOurClient(boolean telegramUserOurClient) {
        isTelegramUserOurClient = telegramUserOurClient;
    }
}
