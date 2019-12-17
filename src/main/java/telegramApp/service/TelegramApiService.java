package telegramApp.service;

import telegramApp.model.TelegramUser;

public interface TelegramApiService {

    public TelegramUser sendSong(TelegramUser telegramUser);

    public TelegramUser approveSong(TelegramUser telegramUser) ;
}
