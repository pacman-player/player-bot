package bot.app.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import bot.app.model.TelegramMessage;

public interface TelegramMessageRepo extends JpaRepository<TelegramMessage, Long> {
    TelegramMessage findByChatId(long id);
}
