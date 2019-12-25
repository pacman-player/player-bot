package telegramApp.bot;

import telegramApp.model.TelegramMessage;

public class BotContext {

    private final Bot bot;
    private final TelegramMessage telegramMessage;
    private final String input;

    public BotContext(Bot bot, TelegramMessage telegramMessage, String input) {
        this.bot = bot;
        this.telegramMessage = telegramMessage;
        this.input = input;
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


}
