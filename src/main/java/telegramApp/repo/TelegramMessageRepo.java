package telegramApp.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import telegramApp.model.TelegramMessage;

public interface TelegramMessageRepo extends JpaRepository<TelegramMessage, Long> {
    TelegramMessage findByChatId(long id);
}
