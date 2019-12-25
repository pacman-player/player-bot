package telegramApp.service;

import telegramApp.dto.SongRequest;
import telegramApp.dto.SongResponce;

public interface TelegramApiService {

    SongResponce sendAutorAndSongName(SongRequest telegramMessage);

    SongRequest approveSong(SongRequest telegramMessage) ;
}
