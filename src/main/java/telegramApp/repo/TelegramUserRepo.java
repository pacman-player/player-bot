package telegramApp.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import telegramApp.model.TelegramUser;

public interface TelegramUserRepo extends JpaRepository<TelegramUser, Long> {
    TelegramUser findByChatId(long id);
}
