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
import telegramApp.service.TelegramMessageService;

import java.util.ArrayList;
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
        TelegramMessage telegramMessage;
        final long chatId;
        BotContext context;
        BotState state;
        String text;

        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
            telegramMessage = telegramMessageService.findByChatId(chatId);

            if (update.getMessage().hasText()) {
                text = update.getMessage().getText();
            } else {
                text = "";
            }

            if (telegramMessage == null) {
                state = BotState.getInitialState();
                telegramMessage = new TelegramMessage(update.getMessage().getFrom(), state.ordinal());
                telegramMessageService.addTelegramUser(telegramMessage);
                context = new BotContext(this, telegramMessage, text, update);
                state.enter(context);
            } else {
                if (text.equals("/start")) {
                    state = BotState.getInitialState();
                } else {
                    state = BotState.byId(telegramMessage.getStateId());
                }

                if (state.name().equals("Payment")) {
                    context = new BotContext(this, telegramMessage, text, update.getMessage().getSuccessfulPayment());
                } else {
                    context = new BotContext(this, telegramMessage, text);
                }
            }

            if (update.getMessage().hasLocation()) {
                context = new BotContext(this, telegramMessage, text, update);
                state.handleInput(context, new LocationDto(
                        context.getUpdate().getMessage().getLocation().getLatitude(),
                        context.getUpdate().getMessage().getLocation().getLongitude()));

                telegramMessage.setStateId(state.ordinal());

                // Обозначаем текущего пользователя как реального посетителя,
                // так как он поделился с нами своей геопозицией.
                telegramMessage.setTelegramUserSharedGeolocation(true);
                telegramMessageService.updateTelegramUser(telegramMessage);

                return;
            } else if (state.name().equals("GeoLocation") & !update.getMessage().hasLocation()) {
                state.handleInput(context);

                telegramMessage.setStateId(state.ordinal());
                telegramMessageService.updateTelegramUser(telegramMessage);

                return;
            }

            state.handleInput(context);
            do {
                state = state.nextState();
                state.enter(context);
            }
            while (!state.isInputNeeded());

            telegramMessage.setStateId(state.ordinal());
            telegramMessageService.updateTelegramUser(telegramMessage);
        } else if (update.hasCallbackQuery()) {
            chatId = update.getCallbackQuery().getMessage().getChatId();
            telegramMessage = telegramMessageService.findByChatId(chatId);
            text = update.getCallbackQuery().getMessage().getText();

            state = BotState.byId(telegramMessage.getStateId());

            context = new BotContext(this, telegramMessage, text);

            telegramMessage.setCompanyId(Long.valueOf(update.getCallbackQuery().getData())); //сетим id компании

            // Если этот пользовтель Telegram ранее был определен как реальный посетитель
            // заведения то регистрируем его и факт посещения этого заведения в БД
            if (telegramMessage.isTelegramUserSharedGeolocation() && "GeoLocation".equals(state.name())) {
                registerTelegramUserAndVisit(context.getTelegramMessage());
                telegramMessage.setVisitRegistered(true);
                telegramMessageService.updateTelegramUser(telegramMessage);
            }

            do {
                state = state.nextState();
                state.enter(context);
            }
            while (!state.isInputNeeded());

            telegramMessage.setStateId(state.ordinal());
            telegramMessageService.updateTelegramUser(telegramMessage);
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
    void registerTelegramUserAndVisit(TelegramMessage telegramMessage) {
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