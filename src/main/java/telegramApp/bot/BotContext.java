package telegramApp.bot;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.payments.SuccessfulPayment;
import telegramApp.model.TelegramMessage;

public class BotContext {

    private final Bot bot;
    private final TelegramMessage telegramMessage;
    private final String input;
    private SuccessfulPayment successfulPayment;
    private Update update;

    public BotContext(Bot bot, TelegramMessage telegramMessage, String input) {
        this.bot = bot;
        this.telegramMessage = telegramMessage;
        this.input = input;
    }

    public BotContext(Bot bot, TelegramMessage telegramMessage, String input, SuccessfulPayment successfulPayment) {
        this.bot = bot;
        this.telegramMessage = telegramMessage;
        this.input = input;
        this.successfulPayment = successfulPayment;
    }

    public BotContext(Bot bot, TelegramMessage telegramMessage, String input, Update update) {
        this.bot = bot;
        this.telegramMessage = telegramMessage;
        this.input = input;
        this.update = update;
    }

    public Bot getBot() {
        return bot;
    }

    public TelegramMessage getTelegramMessage() {
        return telegramMessage;
    }

    public String getInput() {
        return input;
    }

    public SuccessfulPayment getSuccessfulPayment() {
        return successfulPayment;
    }

    public Update getUpdate() {
        return update;
    }

    public void setUpdate(Update update) {
        this.update = update;
    }
}
