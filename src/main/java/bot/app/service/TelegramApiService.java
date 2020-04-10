package bot.app.service;

import bot.app.dto.LocationDto;
import bot.app.dto.SongRequest;
import bot.app.dto.SongResponse;
import bot.app.dto.VisitDto;

import java.util.List;

public interface TelegramApiService {

    SongResponse sendAuthorAndSongName(SongRequest telegramMessage);

    List sendGeoLocation(LocationDto locationDto);

    List getAllCompany();

    void addSongToQueue(long songId, long companyId);

    SongResponse approveSong(SongRequest telegramMessage);

    /**
     * Метод регистрирует пользователя Telegram и факт посещения этим пользователем
     * заведения в нашей базе данных на сервере pacman-player-core.
     * @param visitDto
     */
    void registerTelegramUserAndVisit(VisitDto visitDto);
}
