package telegramApp.bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.games.CallbackGame;
import org.telegram.telegrambots.meta.api.objects.payments.LabeledPrice;
import org.telegram.telegrambots.meta.api.objects.payments.SuccessfulPayment;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiValidationException;
import telegramApp.dto.CompanyDto;
import telegramApp.dto.LocationDto;
import telegramApp.dto.SongResponse;
import telegramApp.model.TelegramMessage;

import java.io.ByteArrayInputStream;
import java.util.*;

@Component
public enum BotState {

    Start(false) {
        @Override
        public void enter(BotContext context) {
            sendMessage(context, "Привет");
        }

        @Override
        public BotState nextState() {
            return GeoLocation;
        }
    },

    GeoLocation() {
        private BotState next;

        @Override
        public void enter(BotContext context) {
            sendMessage(context, "Отправьте местоположение, чтобы бот мог определить ваше заведение \n\nили \n\nвыберите заведение из списка");
        }

        @Override
        public void handleInput(BotContext context, Update update) {
            try {
                context.getBot().execute(sendKeyBoardMessage(update.getMessage().getChatId()));
            } catch (TelegramApiValidationException e) {
                e.printStackTrace();
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void handleInput(BotContext context, LocationDto locationDto) {
            HashMap company = context.getBot().sendGeoLocationToServer(locationDto);

            if(company.isEmpty()){
                sendMessage(context, "Не удалось получить геоданные. Попробуйте выбрать заведение из списка вручную.");
            }

            CompanyDto companyDto = new CompanyDto(1l, (Integer) company.get("1"), (String) company.get("2"));
            context.getTelegramMessage().setCompanyId(Long.valueOf((companyDto.getCompanyId())));
            System.out.println(context.getTelegramMessage().getCompanyId());
            sendMessage(context, "Список заведений: \n" + companyDto.getName());

            try {
                context.getBot().execute(sendInlineKeyBoardMessageListOfCompanies(context.getTelegramMessage().getChatId(), context.getBot().getAllCompany()));
            } catch (TelegramApiException e) {
                e.getMessage();
            }


//            sendMessage(context, "Список заведений: \n\n1...\n2...\n3...\n\n...");
        }

        @Override
        public void handleInput(BotContext context) {
            context.getBot().getAllCompany();
            sendMessage(context, "Список заведений: \n");

            List<LinkedHashMap<String, String>> listOfCompanies = (List<LinkedHashMap<String, String>>)context.getBot().getAllCompany();

            try {
                context.getBot().execute(sendInlineKeyBoardMessageListOfCompanies(context.getTelegramMessage().getChatId(), listOfCompanies));
            } catch (TelegramApiException e) {
                e.getMessage();
            }


//            sendMessage(context, "Список заведений: \n\n1...\n2...\n3...\n\n...");
        }

        @Override
        public BotState nextState() {
            return EnterPerformerName;
        }
    },

    EnterPerformerName {
        @Override
        public void enter(BotContext context) {
            sendMessage(context, "Введите исполнителя");
        }

        @Override
        public void handleInput(BotContext context) {
            String performerName = context.getInput();
            context.getTelegramMessage().setPerformerName(performerName);
        }

        @Override
        public BotState nextState() {
            return EnterSongName;
        }
    },

    EnterSongName {
        private BotState next;

        @Override
        public void enter(BotContext context) {
            sendMessage(context, "Введите название песни:");
        }

        @Override
        public void handleInput(BotContext context) {
            next = ApproveSong;
            context.getTelegramMessage().setSongName(context.getInput());
            sendMessage(context, "Песня загружается...");
            sendAnimation(context, "https://media.giphy.com/media/QCJvAY0aFxZgPn1Ok1/giphy.gif", 20, 20);
            sendAction(context, ActionType.UPLOADAUDIO);
            try {
                SongResponse songResponse = context.getBot().approveToServer(context.getTelegramMessage());
                Long songId = songResponse.getSongId();
                TelegramMessage telegramMessage = context.getTelegramMessage();
                telegramMessage.setSongId(songId);
                context.getBot().saveTelegramMessage(telegramMessage);
                sendTrack(context, songResponse);
            } catch (Exception ex) {
                ex.printStackTrace();
                sendMessage(context, "Такая песня не найдена");
                next = EnterPerformerName;
            }
        }

        @Override
        public BotState nextState() {
            return next;
        }
    },

    //Юзер получил звуковой файл для того чтобы уточнить нужная ли это песня.
    //Юзер должен нажать "да" если это та песня.
    ApproveSong() {
        private BotState next;

        @Override
        public void enter(BotContext context) {
            ReplyKeyboardMarkup customReplyKeyboardMarkup = context.getBot().getCustomReplyKeyboardMarkup();
            SendMessage message = new SendMessage()
                    .setChatId(context.getTelegramMessage().getChatId())
                    .enableMarkdown(true)
                    .setReplyMarkup(customReplyKeyboardMarkup)
                    .setText("Это нужная песня?");
            try {
                context.getBot().execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        public void handleInput(BotContext context) {
            String text = context.getInput();
            if (text.equals("Да")) {
                SongResponse songResponse = context.getBot().sendToServer(context.getTelegramMessage());
                sendTrack(context, songResponse);
                next = Payment;
            } else {
                next = EnterPerformerName;
            }
        }

        @Override
        public BotState nextState() {
            return next;
        }
    },

    Payment {
        private BotState next;

        @Override
        public void enter(BotContext context) {
            SendInvoice invoice = new SendInvoice();
            invoice.setChatId(context.getTelegramMessage().getChatId().intValue());
            invoice.setProviderToken(context.getBot().getProviderToken());
            invoice.setTitle("Оплата услуги");
            invoice.setDescription("Для добавления песни в очередь пожалуйста оплатите услугу.");
            invoice.setPayload("pacman-player" + context.getTelegramMessage().getChatId());
            invoice.setStartParameter("payment-invoice");
            invoice.setCurrency("RUB");
            List<LabeledPrice> labeledPrices = new ArrayList<>();
            labeledPrices.add(new LabeledPrice("1 song", 6500));
            invoice.setPrices(labeledPrices);

            try {
                context.getBot().execute(invoice);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void handleInput(BotContext context) {
            SuccessfulPayment successfulPayment = context.getSuccessfulPayment();
            if (successfulPayment != null && successfulPayment.getInvoicePayload().startsWith("pacman-player")) {
                sendMessage(context, "Спасибо за оплату");
                TelegramMessage telegramMessage = context.getBot().getTelegramMessageFromDB(context.getTelegramMessage().getChatId());
                context.getBot().sendSongIdToServer(telegramMessage);
                next = Approved;
            } else {
                sendMessage(context, "Оплата не прошла, попробуйте снова");
                next = Payment;
            }
        }

        @Override
        public BotState nextState() {
            return next;
        }
    },

    Approved(false) {
        @Override
        public void enter(BotContext context) {
            try {
                context.getBot().sendSongIdToServer(context.getTelegramMessage());
                sendMessage(context, "Песня добавлена в очередь. Вы можете заказать ещё одну.");
            } catch (Exception ex) {
                ex.printStackTrace();
                sendMessage(context, "Что-то пошло не так");
            }
        }

        @Override
        public BotState nextState() {
            return EnterPerformerName;
        }
    };

    private static BotState[] states;
    private final boolean inputNeeded;

    BotState() {
        this.inputNeeded = true;
    }

    BotState(boolean inputNeeded) {
        this.inputNeeded = inputNeeded;
    }

    public static SendMessage sendKeyBoardMessage(long chatId) throws TelegramApiValidationException {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        KeyboardButton keyboardButton1 = new KeyboardButton();
        KeyboardButton keyboardButton2 = new KeyboardButton();

        keyboardButton1.setText("Отправить местоположение");
        keyboardButton2.setText("Показать список заведений");

        keyboardButton1.setRequestLocation(true);

        KeyboardRow keyboardButtonsRow = new KeyboardRow();

        keyboardButtonsRow.add(keyboardButton1);
        keyboardButtonsRow.add(keyboardButton2);

        List<KeyboardRow> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow);

        keyboardMarkup.setKeyboard(rowList);

        SendMessage sendMessage = new SendMessage().setChatId(chatId).setText("Выберите ваш вариант:").setReplyMarkup(keyboardMarkup);

        return sendMessage;
    }

    public static SendMessage sendInlineKeyBoardMessageListOfCompanies(long chatId, Object listOfCompanies) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();


        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton();
        InlineKeyboardButton inlineKeyboardButton2 = new InlineKeyboardButton();

        inlineKeyboardButton1.setText("Company_1");
        inlineKeyboardButton1.setCallbackData("Button \"Company_1\" has been pressed");

        inlineKeyboardButton2.setText("Company_2");
        inlineKeyboardButton2.setCallbackData("Button \"Company_2\" has been pressed");

        List<InlineKeyboardButton> keyboardButtonsRow1 = new ArrayList<>();
        List<InlineKeyboardButton> keyboardButtonsRow2 = new ArrayList<>();

        keyboardButtonsRow1.add(inlineKeyboardButton1);

        keyboardButtonsRow2.add(inlineKeyboardButton2);

        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();
        rowList.add(keyboardButtonsRow1);
        rowList.add(keyboardButtonsRow2);

        inlineKeyboardMarkup.setKeyboard(rowList);

        SendMessage sendMessage = new SendMessage().setChatId(chatId).setText("Список заведений:").setReplyMarkup(inlineKeyboardMarkup);

        return sendMessage;
    }

    public static BotState getInitialState() {
        return byId(0);
    }

    public static BotState byId(int id) {
        if (states == null) {
            states = BotState.values();
        }
        return states[id];
    }

    protected void sendMessage(BotContext context, String text) {
        SendMessage message = new SendMessage()
                .setChatId(context.getTelegramMessage().getChatId())
                .setText(text);
        try {
            context.getBot().execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    protected void sendTrack(BotContext context, SongResponse songResponse) {
        SendAudio sendAudio = new SendAudio();
        sendAudio.setAudio(songResponse.getTrackName(), new ByteArrayInputStream(songResponse.getTrack()));
        sendAudio.setChatId(context.getTelegramMessage().getChatId());

        try {
            context.getBot().execute(sendAudio);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    //показывает действия собеседника
    protected void sendAction(BotContext context, ActionType actionType){
        SendChatAction sendAction = new SendChatAction();
        sendAction.setAction(actionType);
        sendAction.setChatId(context.getTelegramMessage().getChatId());

        try {
            context.getBot().execute(sendAction);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    protected void sendAnimation(BotContext context, String url, int width, int height){
        SendAnimation sendAnimation = new SendAnimation();
        sendAnimation.setChatId(context.getTelegramMessage().getChatId());
        sendAnimation.setAnimation(url);
        sendAnimation.setWidth(width);
        sendAnimation.setHeight(height);

        try {
            context.getBot().execute(sendAnimation);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public boolean isInputNeeded() {
        return inputNeeded;
    }

    public void handleInput(BotContext context) {}
    public void handleInput(BotContext context, Update update) {}
    public void handleInput(BotContext context, LocationDto locationDto) {}

    public abstract void enter(BotContext context);

    public abstract BotState nextState();
}