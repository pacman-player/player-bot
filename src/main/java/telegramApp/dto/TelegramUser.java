package telegramApp.dto;

import org.telegram.telegrambots.meta.api.objects.User;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.util.Objects;

/**
 * Класс, описывающий посетителя заведения - пользователя мессенджера Telegram,
 * обратившегося к нашему боту для заказа песни. Схож с объектом User из Telegram API.
 * Чтобы специально не создавать под него таблицу, решено "встроить" его в таблицу
 * TelegramMessage.
 */

@Embeddable
public class TelegramUser {

    @Column(name = "t_u_id")
    private Long id;

    @Column(name = "t_u_first_name")
    private String firstName;

    @Column(name = "t_u_last_name")
    private String lastName;

    @Column(name = "t_u_name")
    private String userName;

    @Column(name = "t_u_lang")
    private String languageCode;

    @Column(name = "is_t_u_bot")
    private Boolean isBot;

    public TelegramUser() {
    }

    public TelegramUser(Long id, String firstName, String lastName, String userName, String languageCode, Boolean isBot) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.languageCode = languageCode;
        this.isBot = isBot;
    }

    public TelegramUser(User user) {
        this.id = Long.valueOf(user.getId());
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.userName = user.getUserName();
        this.languageCode = user.getLanguageCode();
        this.isBot = user.getBot();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public Boolean getBot() {
        return isBot;
    }

    public void setBot(Boolean bot) {
        isBot = bot;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TelegramUser)) return false;
        TelegramUser that = (TelegramUser) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TelegramUser{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", userName='" + userName + '\'' +
                ", languageCode='" + languageCode + '\'' +
                ", isBot=" + isBot +
                '}';
    }
}