package telegramApp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import telegramApp.dto.SongRequest;
import telegramApp.dto.SongResponse;

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

    public SongResponse approveSong(SongRequest songRequest) {
        String URL = serverPath + "/api/tlg/approve";
        return restTemplate.postForObject(URL, songRequest, SongResponse.class);
    }

    public void addSongToQueue(long songId, long companyId) {
        String URL = serverPath + "/api/tlg/addSongToQueue";
        HttpHeaders headers = new HttpHeaders();
        headers.add("songId", String.valueOf(songId));
        headers.add("companyId", String.valueOf(companyId));
        HttpEntity httpEntity = new HttpEntity(headers);
        restTemplate.postForObject(URL, httpEntity, Void.class);
    }

}
