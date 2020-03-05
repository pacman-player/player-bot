package telegramApp.dto;

import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Objects;

/**
 * Класс, описывающий посетителя заведения - пользователя мессенджера Telegram,
 * обратившегося к нашему боту для заказа песни. Объект данного класса предназначен
 * для отправки на сервер pacman-player-core. Схож с объектом User из Telegram API.
 */
public class TelegramUser {

    private Long id;
    private String firstName;
    private Boolean isBot;
    private String lastName;
    private String userName;
    private String languageCode;

    public TelegramUser() {}

    public TelegramUser(Long id, String firstName, Boolean isBot, String lastName, String userName, String languageCode) {
        this.id = id;
        this.firstName = firstName;
        this.isBot = isBot;
        this.lastName = lastName;
        this.userName = userName;
        this.languageCode = languageCode;
    }

    // Конструктор для быстрого преобразования объекта "пользователя мессенджера Telegram" (из Telegram API)
    // в наш объект TelegramUser
    public TelegramUser(User user) {
        this.id = new Long(user.getId());
        this.firstName = user.getFirstName();
        this.isBot = user.getBot();
        this.lastName = user.getLastName();
        this.userName = user.getUserName();
        this.languageCode = user.getLanguageCode();
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

    public Boolean getBot() {
        return isBot;
    }

    public void setBot(Boolean bot) {
        isBot = bot;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TelegramUser telegramUser = (TelegramUser) o;
        return Objects.equals(id, telegramUser.id) &&
                Objects.equals(firstName, telegramUser.firstName) &&
                Objects.equals(isBot, telegramUser.isBot) &&
                Objects.equals(lastName, telegramUser.lastName) &&
                Objects.equals(userName, telegramUser.userName) &&
                Objects.equals(languageCode, telegramUser.languageCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, isBot, lastName, userName, languageCode);
    }

    @Override
    public String toString() {
        return "TelegramUser{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", isBot=" + isBot +
                ", lastName='" + lastName + '\'' +
                ", userName='" + userName + '\'' +
                ", languageCode='" + languageCode + '\'' +
                '}';
    }
}
