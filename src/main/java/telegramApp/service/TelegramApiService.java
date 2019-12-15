package telegramApp.service;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import telegramApp.model.TelegramUser;

@Service
public class TelegramApiService {
    private RestTemplate restTemplate;

    public TelegramApiService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder
                .build();
    }

    public TelegramUser sendSong(TelegramUser telegramUser) {
        String URL = "http://localhost:8080/tlg/song";
        return restTemplate.postForObject(URL, telegramUser, TelegramUser.class);
    }

    public TelegramUser approveSong (TelegramUser telegramUser) {
        String URL = "http://localhost:8080/tlg/approve";
        return restTemplate.postForObject(URL, telegramUser, TelegramUser.class);
    }

}
