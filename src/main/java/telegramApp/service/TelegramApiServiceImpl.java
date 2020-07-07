package telegramApp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import telegramApp.dto.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@PropertySource("classpath:telegram.properties")
public class TelegramApiServiceImpl implements TelegramApiService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramApiServiceImpl.class);
    private RestTemplate restTemplate;

    @Value("${server.path}")
    private String serverPath;

    // Установим логин и пароль, которые будут использоваться при подключении к
    // РЕСТ-контроллерам player-core в рамках базовой аутентификации.
    public TelegramApiServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .basicAuthentication("bot", "bot")
                .build();
    }

    //TODO: remove
//    @Override
//    public SongResponse sendAuthorAndSongName(SongRequest songRequest) {
//        String URL = serverPath + "/api/tlg/song";
//        return restTemplate.postForObject(URL, songRequest, SongResponse.class);
//    }

    @Override
    @Async
    public CompletableFuture<List> sendGeoLocation(LocationDto locationDto) {
        String URL = serverPath + "/api/tlg/location";
        return CompletableFuture.completedFuture(restTemplate.postForObject(URL, locationDto, List.class));
    }

    @Override
    @Async
    public CompletableFuture<List> getAllCompanies() {
        String URL = serverPath + "/api/tlg/all_company";
        return CompletableFuture.completedFuture(restTemplate.postForObject(URL, null, List.class));
    }

    /**
     * Метод передает на сервер pacman-player-core инфу о песне которую нужно найти. На сервере происодит
     * происк песни по тэгам в БД. Если подходящие песни найдены, то формируется упорядоченный список из их
     * названий и id и передается боту. Непосредственно треки для воспроизведения не передается.
     *
     * @param songRequest
     * @return
     */
    @Override
    @Async
    public CompletableFuture<SongsListResponse> databaseSearch(SongRequest songRequest) {
        String URL = serverPath + "/api/tlg/database_search";
        LOGGER.info("РЕКВЕСТ = {}-{}", songRequest.getAuthorName(), songRequest.getSongName());
        SongsListResponse list = restTemplate.postForObject(URL, songRequest, SongsListResponse.class);
        LOGGER.info("ОТВЕТ СОДЕРЖИТ СПИСОК ИЗ {} ПЕСЕН", list.getSongs().size());
        return CompletableFuture.completedFuture(list);
    }

    /**
     * Метод загружает с сервера pacman-player-core 30-секундный отрывок песни по её id
     * @param songRequest
     * @return
     */
    @Override
    @Async
    public CompletableFuture<SongResponse> loadSong(SongRequest songRequest) {
        String URL = serverPath + "/api/tlg/song";
        LOGGER.info("РЕКВЕСТ = SongId={}", songRequest.getSongId());
        SongResponse test = restTemplate.postForObject(URL, songRequest, SongResponse.class);
        LOGGER.info("ОТВЕТ = {}", test.getTrackName());
        return CompletableFuture.completedFuture(test);
    }

    /**
     * Метод передает на сервер pacman-player-core инфу о песне которую нужно найти. На сервере песня если
     * скачивается с одного из сервисов по поиску музыки - сохраняется в папку music/ и возвращается в бота с инфой
     * о том какой id у песни на сервере, с 30сек отрезком и полным названием трека.
     *
     * @param songRequest
     * @return
     */
    @Override
    @Async
    public CompletableFuture<ResponseEntity<SongResponse>> servicesSearch(SongRequest songRequest) {
        String URL = serverPath + "/api/tlg/services_search";
        LOGGER.info("РЕКВЕСТ = {}-{}", songRequest.getAuthorName(), songRequest.getSongName());
        ResponseEntity<SongResponse> response = restTemplate.postForEntity(URL, songRequest, SongResponse.class);
        LOGGER.info("ОТВЕТ = {}", response.getBody() == null ? "пусто" : response.getBody().getTrackName());
        return CompletableFuture.completedFuture(response);
    }

    /**
     * Метод добавляющий утвержденную песню в очередь SongQueue на сервере pacman-player-core после ее оплаты.
     *
     * @param songId
     * @param companyId
     */
    @Override
    @Async
    public void addSongToQueue(long songId, long companyId) {
        String URL = serverPath + "/api/tlg/addSongToQueue";
        HttpHeaders headers = new HttpHeaders();
        headers.add("songId", String.valueOf(songId));
        headers.add("companyId", String.valueOf(companyId));
        HttpEntity httpEntity = new HttpEntity(headers);
        restTemplate.postForObject(URL, httpEntity, Void.class);
    }

    @Override
    @Async
    public void registerTelegramUserAndVisit(VisitDto visitDto) {
        String URL = serverPath + "/api/tlg/registerTelegramUserAndVisit";
        restTemplate.postForObject(URL, visitDto, Void.class);
    }
}