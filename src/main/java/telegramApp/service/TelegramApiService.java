package telegramApp.service;

import telegramApp.dto.*;

import java.util.List;

public interface TelegramApiService {

    SongResponse sendAuthorAndSongName(SongRequest telegramMessage);

    List sendGeoLocation(LocationDto locationDto);

    List getAllCompany();

    void addSongToQueue(long songId, long companyId);

    SongResponse approveSong(SongRequest telegramMessage);

    void registerUserAndVisit(VisitDto visitDto);
}
