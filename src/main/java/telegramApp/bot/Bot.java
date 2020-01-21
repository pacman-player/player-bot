package telegramApp.bot;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerPreCheckoutQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.payments.PreCheckoutQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import telegramApp.dto.CompanyDto;
import telegramApp.dto.LocationDto;
import telegramApp.dto.SongRequest;
import telegramApp.dto.SongResponse;
import telegramApp.model.TelegramMessage;
import telegramApp.service.TelegramApiService;
import telegramApp.service.TelegramMessageService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;

@Component
@PropertySource("classpath:telegram.properties")
public class Bot extends TelegramLongPollingBot {

    private final TelegramMessageService telegramMessageService;

    private final TelegramApiService telegramApiService;

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
        BotContext context;
        BotState state;
        String text;

        if (!update.hasMessage()) {
            paymentPreCheckout(update);

            if(update.hasCallbackQuery()){
                try {
                    execute(new SendMessage().setText(
                            update.getCallbackQuery().getData())
                            .setChatId(update.getCallbackQuery().getMessage().getChatId()));
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }

            return;
        }

        if (update.getMessage().hasText()) {
            text = update.getMessage().getText();
        } else {
            text = "";
        }

        final long chatId = update.getMessage().getChatId();
        TelegramMessage telegramMessage = telegramMessageService.findByChatId(chatId);

        if (text.equals("/start")) {
            telegramMessage = null;
        }

        if (telegramMessage == null) {
            state = BotState.getInitialState();
            telegramMessage = new TelegramMessage(chatId, state.ordinal());
            telegramMessageService.addTelegramUser(telegramMessage);

            context = new BotContext(this, telegramMessage, text);
            state.enter(context);
        } else {
            if (text.equals("Отмена")) {
                state = BotState.Start;
            } else {
                state = BotState.byId(telegramMessage.getStateId());
            }

            if (state.name().equals("Payment")) {
                context = new BotContext(this, telegramMessage, text, update.getMessage().getSuccessfulPayment());
            } else {
                context = new BotContext(this, telegramMessage, text);
            }
        }

        if (state.name().equals("GeoLocation") & update.getMessage().getLocation() != null) {
            state.handleInput(context, new LocationDto(update.getMessage().getLocation().getLatitude(), update.getMessage().getLocation().getLongitude()));
        } else {
            state.handleInput(context);
        }

        do {
            state = state.nextState();
            state.enter(context);

            if (text.equals("/start")) {
                state.handleInput(context, update);
            }
        }
        while (!state.isInputNeeded());

        telegramMessage.setStateId(state.ordinal());
        telegramMessageService.updateTelegramUser(telegramMessage);
    }

    private void paymentPreCheckout(Update update) {
        boolean success;
        AnswerPreCheckoutQuery answer = new AnswerPreCheckoutQuery();
        PreCheckoutQuery query = update.getPreCheckoutQuery();
        if (query != null && query.getInvoicePayload().startsWith("pacman-player")) {
            answer.setOk(true);
            answer.setPreCheckoutQueryId(query.getId());
            success = true;
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

    List getAllCompany() {
        return telegramApiService.getAllCompany();
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