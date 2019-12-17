package telegramApp.service;

import telegramApp.model.TelegramUser;

public interface TelegramUserService {

    TelegramUser findByChatId(long id);

    void deleteByChatId(Long id);

    void addTelegramUser(TelegramUser telegramUser);
}
