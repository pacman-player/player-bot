package bot.app.service;

import bot.app.model.TelegramMessage;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface TelegramMessageService {

    TelegramMessage findByChatId(long id);

    void deleteByChatId(Long id);

    void addTelegramUser(TelegramMessage telegramMessage);

    void updateTelegramUser(TelegramMessage telegramMessage);

//    void setPositionInQueue(Long position);
}
