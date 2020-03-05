package telegramApp.service;

import telegramApp.dto.LocationDto;
import telegramApp.dto.SongRequest;
import telegramApp.dto.SongResponse;
import telegramApp.dto.TelegramUser;

import java.util.List;

public interface TelegramApiService {

    SongResponse sendAuthorAndSongName(SongRequest telegramMessage);

    List sendGeoLocation(LocationDto locationDto);

    List getAllCompany();

    void addSongToQueue(long songId, long companyId);

    SongResponse approveSong(SongRequest telegramMessage);

    boolean isTelegramUserExists(Long telegramUserId);

    void addTelegramUser(TelegramUser telegramUser);
}
