package telegramApp.service;

import org.telegram.telegrambots.meta.api.objects.Update;
import telegramApp.bot.Bot;
import telegramApp.bot.BotContext;
import telegramApp.bot.BotState;
import telegramApp.dto.LocationDto;
import telegramApp.model.TelegramMessage;

public class TelegramMessageReciever implements Runnable {

    private final Bot bot;
    private final Update update;
    private final TelegramMessageService telegramMessageService;

    private final TelegramApiService telegramApiService;

    public TelegramMessageReciever(Bot bot, Update update, TelegramMessageService telegramMessageService, TelegramApiService telegramApiService) {
        this.bot = bot;
        this.update = update;
        this.telegramMessageService = telegramMessageService;
        this.telegramApiService = telegramApiService;
    }

    @Override
    public void run() {
        TelegramMessage telegramMessage;
        final long chatId;
        BotContext context;
        BotState state;
        String text;

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
            context = new BotContext(bot, telegramMessage, text, update);
            state.enter(context);
        } else {
            if (text.equals("/start")) {
                state = BotState.getInitialState();
            } else {
                state = BotState.byId(telegramMessage.getStateId());
            }

            if (state.name().equals("Payment")) {
                context = new BotContext(bot, telegramMessage, text, update.getMessage().getSuccessfulPayment());
            } else {
                context = new BotContext(bot, telegramMessage, text);
            }
        }

        if (update.getMessage().hasLocation()) {
            context = new BotContext(bot, telegramMessage, text, update);
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

        }


    }

