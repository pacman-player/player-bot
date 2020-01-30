package telegramApp.service;

import org.springframework.stereotype.Service;
import telegramApp.model.TelegramMessage;
import telegramApp.repo.TelegramMessageRepo;

import javax.transaction.Transactional;

@Service
public class TelegramMessageServiceImpl implements TelegramMessageService {
    private TelegramMessageRepo telegramMessageRepo;

    public TelegramMessageServiceImpl(TelegramMessageRepo telegramMessageRepo){
        this.telegramMessageRepo = telegramMessageRepo;
    }

    @Transactional
    public TelegramMessage findByChatId(long id){
        return telegramMessageRepo.findByChatId(id);
    }

    public void deleteByChatId (Long id){
        telegramMessageRepo.delete(telegramMessageRepo.findByChatId(id));
    }
    @Transactional
    public void addTelegramUser (TelegramMessage telegramMessage){
        telegramMessageRepo.save(telegramMessage);
    }

    @Transactional
    public void updateTelegramUser(TelegramMessage telegramMessage) {
        telegramMessageRepo.save(telegramMessage);
    }
}
