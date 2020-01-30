package telegramApp.service;

import org.springframework.transaction.annotation.Transactional;
import telegramApp.model.TelegramMessage;

@Transactional
public interface TelegramMessageService {

    TelegramMessage findByChatId(long id);

    void deleteByChatId(Long id);

    void addTelegramUser(TelegramMessage telegramMessage);

    void updateTelegramUser(TelegramMessage telegramMessage);
}
