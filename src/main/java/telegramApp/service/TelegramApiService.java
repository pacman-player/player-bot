package telegramApp.service;

import telegramApp.dto.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface TelegramApiService {

    SongResponse sendAuthorAndSongName(SongRequest telegramMessage);

    CompletableFuture<List> sendGeoLocation(LocationDto locationDto);

    CompletableFuture<List> getAllCompanies();

    void addSongToQueue(long songId, long companyId);

    CompletableFuture<SongResponse> approveSong(SongRequest telegramMessage);

    /**
     * Метод регистрирует пользователя Telegram и факт посещения этим пользователем
     * заведения в нашей базе данных на сервере pacman-player-core.
     *
     * @param visitDto
     */
    void registerTelegramUserAndVisit(VisitDto visitDto);
}
