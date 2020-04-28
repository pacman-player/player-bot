package telegramApp.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.payments.PreCheckoutQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import telegramApp.dto.*;
import telegramApp.model.TelegramMessage;
import telegramApp.service.TelegramApiService;
import telegramApp.service.TelegramMessageReciever;
import telegramApp.service.TelegramMessageSendler;
import telegramApp.service.TelegramMessageService;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Component
@PropertySource("classpath:telegram.properties")
public class Bot extends TelegramLongPollingBot {

    private final TelegramMessageService telegramMessageService;

    private final TelegramApiService telegramApiService;

    public final Queue<Object> sendQueue = new ConcurrentLinkedQueue<>();
    public final Queue<Object> receiveQueue = new ConcurrentLinkedQueue<>();

    @Value("${bot.name}")
    private String botName;

    @Value("${bot.token}")
    private String botToken;

    @Value("${bot.providerToken}")
    private String providerToken;

    public Bot(TelegramMessageService telegramMessageService, TelegramApiService telegramApiService) {
        this.telegramMessageService = telegramMessageService;
        this.telegramApiService = telegramApiService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        TelegramMessage telegramMessage;
        final long chatId;
        BotContext context;
        BotState state;
        String text;

//        if(update.hasMessage()) {
//            ThreadClass thread = new ThreadClass(update.getMessage());
//        } else  if(update.hasCallbackQuery()) {
//            AnswerCallbackThread answerThread = new AnswerCallbackThread(update.getCallbackQuery());
//        }

        if (update.hasMessage()) {

            TelegramMessageReciever messageReciever = new TelegramMessageReciever(this, update, telegramMessageService, telegramApiService);
            messageReciever.run();


        } else if (update.hasCallbackQuery()) {

            TelegramMessageSendler messageSendler = new TelegramMessageSendler(this, update, telegramMessageService, telegramApiService);
            messageSendler.run();
        } else if (update.hasPreCheckoutQuery()) {
            paymentPreCheckout(update);
        }
    }

    /**
     * Метод регистрирует в нашей базе данных на сервере pacman-player-core
     * пользователя Telegram и факт посещения этим пользователем заведения
     *
     * @param telegramMessage
     */
    public void registerTelegramUserAndVisit(TelegramMessage telegramMessage) {
        TelegramUser telegramUser = telegramMessage.getTelegramUser();
        Long companyId = telegramMessage.getCompanyId();
        VisitDto visitDto = new VisitDto(telegramUser, companyId);
        telegramApiService.registerTelegramUserAndVisit(visitDto);
    }

    private void paymentPreCheckout(Update update) {
        boolean success;
        AnswerPreCheckoutQuery answer = new AnswerPreCheckoutQuery();
        PreCheckoutQuery query = update.getPreCheckoutQuery();
        if (query != null && query.getInvoicePayload().startsWith("pacman-player")) {
            answer.setOk(true);
            answer.setPreCheckoutQueryId(query.getId());
            success = true;

            // Если этот пользовтель Telegram ранее был определен как реальный посетитель заведения,
            // то после выбора заведения он был внесен в нашу БД и вносить его ещё раз не нужно.
            TelegramMessage telegramMessage = telegramMessageService.findByChatId(update.getPreCheckoutQuery().getFrom().getId());
            if (!telegramMessage.isVisitRegistered()) {
                registerTelegramUserAndVisit(telegramMessage);
                telegramMessage.setVisitRegistered(true);
                telegramMessageService.updateTelegramUser(telegramMessage);
            }
        } else {
            answer.setOk(false);
            answer.setErrorMessage("Что-то пошло не так, попробуйте сначала");
            success = false;
        }
        try {
            execute(answer);
        } catch (TelegramApiException e) {
            e.printStackTrace();
            success = false;
        }
        if (!success) {
            long chatId = Long.parseLong(update.getPreCheckoutQuery().getInvoicePayload().substring(13));
            TelegramMessage telegramMessage = telegramMessageService.findByChatId(chatId);
            BotContext context = new BotContext(this, telegramMessage, "");
            BotState.Payment.enter(context);
            telegramMessage.setStateId(BotState.Payment.ordinal());
            telegramMessageService.updateTelegramUser(telegramMessage);
        }
    }

    SongResponse sendToServer(TelegramMessage telegramMessage) {
        SongRequest songRequest = new SongRequest(telegramMessage);
        return telegramApiService.sendAuthorAndSongName(songRequest);
    }

    SongResponse approveToServer(TelegramMessage telegramMessage) {
        SongRequest songRequest = new SongRequest(telegramMessage);
        return telegramApiService.approveSong(songRequest);
    }

    void sendSongIdToServer(TelegramMessage telegramMessage) {
        SongRequest songRequest = new SongRequest(telegramMessage);
        telegramApiService.approveSong(songRequest);
    }

    List sendGeoLocationToServer(LocationDto locationDto) {
        return telegramApiService.sendGeoLocation(locationDto);
    }

    void addSongToQueue(TelegramMessage telegramMessage) {
        telegramApiService.addSongToQueue(telegramMessage.getSongId(), telegramMessage.getCompanyId());
    }

    List getAllCompany() {
        return telegramApiService.getAllCompanies();
    }

    TelegramMessage getTelegramMessageFromDB(Long chatId) {
        return telegramMessageService.findByChatId(chatId);
    }

    void saveTelegramMessage(TelegramMessage telegramMessage) {
        telegramMessageService.updateTelegramUser(telegramMessage);
    }

    //display keyboard buttons
    ReplyKeyboardMarkup getCustomReplyKeyboardMarkup() {
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        replyKeyboardMarkup.setSelective(true);
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setOneTimeKeyboard(true);
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow keyboardRow1 = new KeyboardRow();
        keyboardRow1.add(new KeyboardButton("Да"));
        keyboardRow1.add(new KeyboardButton("Нет"));
        KeyboardRow keyboardRow2 = new KeyboardRow();
        keyboardRow2.add(new KeyboardButton("Отмена"));
        keyboard.add(keyboardRow1);
        keyboard.add(keyboardRow2);
        replyKeyboardMarkup.setKeyboard(keyboard);
        return replyKeyboardMarkup;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    public String getProviderToken() {
        return providerToken;
    }
}