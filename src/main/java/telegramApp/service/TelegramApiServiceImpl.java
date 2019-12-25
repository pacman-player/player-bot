package telegramApp.service;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import telegramApp.dto.SongRequest;
import telegramApp.dto.SongResponce;

@Service
public class TelegramApiServiceImpl implements TelegramApiService {
    private RestTemplate restTemplate;

    public TelegramApiServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .build();
    }

    public SongResponce sendAutorAndSongName(SongRequest songRequest) {
        String URL = "http://localhost:8080/api/tlg/song";
        return restTemplate.postForObject(URL, songRequest, SongResponce.class);
    }

    public SongRequest approveSong(SongRequest telegramMessage) {
        String URL = "http://localhost:8080/api/tlg/approve";
        return restTemplate.postForObject(URL, telegramMessage, SongRequest.class);
    }

}
