package telegramApp.service;

import org.springframework.http.ResponseEntity;
import telegramApp.dto.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface TelegramApiService {

    //TODO: remove
//    SongResponse sendAuthorAndSongName(SongRequest telegramMessage);

    CompletableFuture<List> sendGeoLocation(LocationDto locationDto);

    CompletableFuture<List> getAllCompanies();

    void addSongToQueue(long songId, long companyId);

    CompletableFuture<SongsListResponse> databaseSearch(SongRequest songRequest);

    CompletableFuture<SongResponse> loadSong(SongRequest songRequest);

    CompletableFuture<ResponseEntity<SongResponse>> servicesSearch(SongRequest songRequest);

    /**
     * Метод регистрирует пользователя Telegram и факт посещения этим пользователем
     * заведения в нашей базе данных на сервере pacman-player-core.
     *
     * @param visitDto
     */
    void registerTelegramUserAndVisit(VisitDto visitDto);
}
