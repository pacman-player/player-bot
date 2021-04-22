package telegramApp.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import telegramApp.model.TelegramMessage;

@Repository
public interface TelegramMessageRepo extends JpaRepository<TelegramMessage, Long> {
    TelegramMessage findByChatId(long id);
}
