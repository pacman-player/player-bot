package telegramApp.service;

import telegramApp.dto.LocationDto;
import telegramApp.dto.SongRequest;
import telegramApp.dto.SongResponse;

public interface TelegramApiService {

    SongResponse sendAuthorAndSongName(SongRequest telegramMessage);

    Long sendGeoLocation(LocationDto locationDto);

    void addSongToQueue(long songId, long companyId);

    SongResponse approveSong(SongRequest telegramMessage) ;
}
