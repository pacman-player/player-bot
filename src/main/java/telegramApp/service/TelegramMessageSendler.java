package telegramApp.service;

import org.telegram.telegrambots.meta.api.objects.Update;
import telegramApp.bot.Bot;
import telegramApp.bot.BotContext;
import telegramApp.bot.BotState;
import telegramApp.dto.LocationDto;
import telegramApp.dto.TelegramUser;
import telegramApp.dto.VisitDto;
import telegramApp.model.TelegramMessage;

public class TelegramMessageSendler implements Runnable {

    private final Bot bot;
    private final Update update;
    private final TelegramMessageService telegramMessageService;

    private final TelegramApiService telegramApiService;

    public TelegramMessageSendler(Bot bot, Update update, TelegramMessageService telegramMessageService, TelegramApiService telegramApiService) {
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

        chatId = update.getCallbackQuery().getMessage().getChatId();
        telegramMessage = telegramMessageService.findByChatId(chatId);
        text = update.getCallbackQuery().getMessage().getText();

        state = BotState.byId(telegramMessage.getStateId());

        context = new BotContext(bot, telegramMessage, text);

        telegramMessage.setCompanyId(Long.valueOf(update.getCallbackQuery().getData())); //сетим id компании

        // Если этот пользовтель Telegram ранее был определен как реальный посетитель
        // заведения то регистрируем его и факт посещения этого заведения в БД
        if (telegramMessage.isTelegramUserSharedGeolocation() && "GeoLocation".equals(state.name())) {
            bot.registerTelegramUserAndVisit(context.getTelegramMessage());
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

}



    }

