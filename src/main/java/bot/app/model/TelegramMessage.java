package bot.app.model;

import bot.app.dto.TelegramUser;
import org.telegram.telegrambots.meta.api.objects.User;

import javax.persistence.*;

@Entity
@Table(name = "telegram_messages")
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
    private TelegramUser telegramUser;

    /**
     * Для принятия решения о записи в базу данных на сервер pacman-player-core
     * факта посещения пользователем Telegram заведения (Company) нам нужно
     * знать, имеем ли мы дело с реальным посетителем заведения или человек
     * просто лазает в нашем боте и нажимает на кнопки. При срабатывании одного из
     * условий нашей бизнес-логики, когда пользователь Telegram поделился с нами
     * своей геопозицией, это поле будет принимать значение true, значит мы
     * считаем, что посетитель реальный и его посещение нужно внести в базу.
     */
    @Column(name = "is_t_u_shared_geo")
    private boolean isTelegramUserSharedGeolocation;

    @Column(name = "is_visit_registered")
    private boolean isVisitRegistered;

    public TelegramMessage() {
    }

    public TelegramMessage(Long chatId, int stateId) {
        this.chatId = chatId;
        this.stateId = stateId;
    }

    public TelegramMessage(User user, int ordinal) {
        this.chatId = Long.valueOf(user.getId());
        this.stateId = ordinal;
        this.telegramUser = new TelegramUser(user);
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

    public TelegramUser getTelegramUser() {
        return telegramUser;
    }

    public void setTelegramUser(TelegramUser telegramUser) {
        this.telegramUser = telegramUser;
    }

    public boolean isTelegramUserSharedGeolocation() {
        return isTelegramUserSharedGeolocation;
    }

    public void setTelegramUserSharedGeolocation(boolean telegramUserSharedGeolocation) {
        isTelegramUserSharedGeolocation = telegramUserSharedGeolocation;
    }

    public boolean isVisitRegistered() {
        return isVisitRegistered;
    }

    public void setVisitRegistered(boolean visitRegistered) {
        isVisitRegistered = visitRegistered;
    }
}
