package telegramApp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import telegramApp.dto.LocationDto;
import telegramApp.dto.SongRequest;
import telegramApp.dto.SongResponse;

import java.util.List;

@Service
@PropertySource("classpath:telegram.properties")
public class TelegramApiServiceImpl implements TelegramApiService {
    private RestTemplate restTemplate;

    @Value("${server.path}")
    private String serverPath;

    public TelegramApiServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .build();
    }

    public SongResponse sendAuthorAndSongName(SongRequest songRequest) {
        String URL = serverPath + "/api/tlg/song";
        return restTemplate.postForObject(URL, songRequest, SongResponse.class);
    }

    @Override
    public List sendGeoLocation(LocationDto locationDto) {
        String URL = serverPath + "/api/tlg/location";
        return restTemplate.postForObject(URL, locationDto, List.class);
    }

    public List getAllCompany() {
        String URL = serverPath + "/api/tlg/all_company";
        return restTemplate.postForObject(URL, null, List.class);
    }

    /**
     * Метод передает на сервер pacman-player-core инфу о песне которую нужно найти. На сервере песня если
     * скачивается с одного из сервисов по поиску музыки - сохраняется в папку music/ и возвращается в бота с инфой
     * о том какой id у песни на сервере, с 30сек отрезком и полным названием трека.
     * @param songRequest
     * @return
     */
    public SongResponse approveSong(SongRequest songRequest) {
        String URL = serverPath + "/api/tlg/approve";
        return restTemplate.postForObject(URL, songRequest, SongResponse.class);
    }

    /**
     * Метод добавляющий утвержденную песню в очередь SongQueue на сервере pacman-player-core после ее оплаты.
     * @param songId
     * @param companyId
     */
    public void addSongToQueue(long songId, long companyId) {
        String URL = serverPath + "/api/tlg/addSongToQueue";
        HttpHeaders headers = new HttpHeaders();
        headers.add("songId", String.valueOf(songId));
        headers.add("companyId", String.valueOf(companyId));
        HttpEntity httpEntity = new HttpEntity(headers);
        restTemplate.postForObject(URL, httpEntity, Void.class);
    }

}
