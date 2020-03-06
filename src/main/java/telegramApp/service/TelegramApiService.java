package telegramApp.service;

import telegramApp.dto.*;

import java.util.List;

public interface TelegramApiService {

    SongResponse sendAuthorAndSongName(SongRequest telegramMessage);

    List sendGeoLocation(LocationDto locationDto);

    List getAllCompany();

    void addSongToQueue(long songId, long companyId);

    SongResponse approveSong(SongRequest telegramMessage);

    boolean isTelegramUserExists(Long telegramUserId);

    void addTelegramUser(TelegramUser telegramUser);

    void registerTelegramUserCompanyVisit(TelegramUserCompanyIdDto telegramUserCompanyIdDto);
}
