package telegramApp.service;

import telegramApp.dto.SongRequest;
import telegramApp.dto.SongResponse;

public interface TelegramApiService {

    SongResponse sendAuthorAndSongName(SongRequest telegramMessage);

    void addSongToQueue(long songId, long companyId);

    SongResponse approveSong(SongRequest telegramMessage) ;
}
