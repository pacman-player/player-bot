package telegramApp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import telegramApp.model.TelegramUser;
import telegramApp.service.TelegramApiService;
import telegramApp.service.TelegramUserService;

@RestController
@RequestMapping(value = "/bot")
public class MainController extends TelegramLongPollingBot {
    private final TelegramUserService telegramUserService;

    private final TelegramApiService telegramApiService;

    @Autowired
    public MainController(TelegramUserService telegramUserService, TelegramApiService telegramApiService) {
        this.telegramUserService = telegramUserService;
        this.telegramApiService = telegramApiService;
    }

    @PostMapping(value = "/song")
    public void add(@RequestBody TelegramUser tlgUser) {

        SendAudio sendAudio = new SendAudio();
        SendMessage response = new SendMessage();
        response.setChatId(tlgUser.getChatId());
        if (tlgUser.getTrack() != null) {

            sendAudio.setAudio(tlgUser.getTrack());
            sendAudio.setChatId(tlgUser.getChatId());

            response.setText("Это нужная песня? (Введите \"да\" если это та песня)");

            telegramUserService.addTelegramUser(tlgUser);

            try {
                execute(sendAudio);
                execute(response);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

        } else {
            String text = " К сожалению такая песня не найдена. Введите другую.";
            response.setText(text);
            try {
                execute(response);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    @PostMapping(value = "/approve")
    public void approved(@RequestBody TelegramUser tlgUser) {

        Long chatId = tlgUser.getChatId();
        SendMessage response = new SendMessage();
        response.setChatId(tlgUser.getChatId());
        response.setText("Всё ок");

        telegramUserService.deleteByChatId(chatId);
        try {
            execute(response);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {

            Message message = update.getMessage();
            Long chatId = message.getChatId();
            String text = message.getText();
            SendMessage response = new SendMessage();
            response.setChatId(chatId);
            if (telegramUserService.findByChatId(chatId) == null) {
                TelegramUser telegramUser = new TelegramUser();
                telegramUser.setChatId(message.getChatId());
                telegramUser.setSongName(message.getText());
                telegramApiService.sendSong(telegramUser);
            } else {
                if (text.equals("да")) {
                    TelegramUser user = telegramUserService.findByChatId(chatId);
                    telegramApiService.approveSong(user);
                } else {
                    telegramUserService.deleteByChatId(chatId);
                    response.setText("Введите название песни");
                    try {
                        execute(response);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    @Override
    public String getBotToken() {
        return "984357723:AAFxB5Vx-l8sl675bG1zcqGwZ1YlznGUSpA";
    }

    @Override
    public String getBotUsername() {
        return "SongName_bot";
    }
}
