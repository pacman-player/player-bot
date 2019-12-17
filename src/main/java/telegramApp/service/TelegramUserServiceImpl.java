package telegramApp.service;

import org.springframework.stereotype.Service;
import telegramApp.model.TelegramUser;
import telegramApp.repo.TelegramUserRepo;

import javax.transaction.Transactional;

@Service
public class TelegramUserServiceImpl implements TelegramUserService {
    private TelegramUserRepo telegramUserRepo;

    public TelegramUserServiceImpl(TelegramUserRepo telegramUserRepo){
        this.telegramUserRepo = telegramUserRepo;
    }

    @Transactional
    public TelegramUser findByChatId(long id){
        return telegramUserRepo.findByChatId(id);
    }

    public void deleteByChatId (Long id){
        telegramUserRepo.delete(telegramUserRepo.findByChatId(id));
    }
    @Transactional
    public void addTelegramUser (TelegramUser telegramUser){
        telegramUserRepo.save(telegramUser);
    }
}
