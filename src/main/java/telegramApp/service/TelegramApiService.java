package telegramApp.service;

import telegramApp.dto.CompanyDto;
import telegramApp.dto.LocationDto;
import telegramApp.dto.SongRequest;
import telegramApp.dto.SongResponse;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

public interface TelegramApiService {

    SongResponse sendAuthorAndSongName(SongRequest telegramMessage);

    HashMap sendGeoLocation(LocationDto locationDto);

    HashMap getAllCompany();

    void addSongToQueue(long songId, long companyId);

    SongResponse approveSong(SongRequest telegramMessage);
}
