package telegramApp.dto;

import telegramApp.model.TelegramMessage;

import java.util.Objects;

/**
 * Класс, описывающий посетителя заведения - пользователя мессенджера Telegram,
 * обратившегося к нашему боту для заказа песни. Объект данного класса предназначен
 * для обмена между pacman-player-core и player-bot. Схож с объектом User из Telegram API.
 */
public class TelegramUserDto {

    private Long id;
    private String firstName;
    private Boolean isBot;
    private String lastName;
    private String userName;
    private String languageCode;

    public TelegramUserDto() {}

    public TelegramUserDto(TelegramMessage telegramMessage) {
        this.id = telegramMessage.getChatId();
        this.firstName = telegramMessage.getTelegramUserFirstName();
        this.isBot = telegramMessage.getTelegramUserBot();
        this.lastName = telegramMessage.getTelegramUserLastName();
        this.userName = telegramMessage.getTelegramUserName();
        this.languageCode = telegramMessage.getTelegramUserLanguageCode();
    }

    public TelegramUserDto(Long id, String firstName, Boolean isBot, String lastName, String userName, String languageCode) {
        this.id = id;
        this.firstName = firstName;
        this.isBot = isBot;
        this.lastName = lastName;
        this.userName = userName;
        this.languageCode = languageCode;
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
        TelegramUserDto telegramUserDto = (TelegramUserDto) o;
        return Objects.equals(id, telegramUserDto.id) &&
                Objects.equals(firstName, telegramUserDto.firstName) &&
                Objects.equals(isBot, telegramUserDto.isBot) &&
                Objects.equals(lastName, telegramUserDto.lastName) &&
                Objects.equals(userName, telegramUserDto.userName) &&
                Objects.equals(languageCode, telegramUserDto.languageCode);
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
