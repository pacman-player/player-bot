package telegramApp.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.payments.LabeledPrice;
import org.telegram.telegrambots.meta.api.objects.payments.SuccessfulPayment;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiValidationException;
import telegramApp.dto.*;
import telegramApp.model.TelegramMessage;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

@Component
public enum BotState {
    Start {
        @Override
        public boolean enter(BotContext context) {
            long chatId = context.getTelegramMessage().getChatId();
            listMap.remove(chatId);
            sendMessage(context, "Привет");
            return false;
        }

        @Override
        public BotState nextState() {
            return GeoLocation;
        }
    },

    GeoLocation {
        @Override
        public boolean enter(BotContext context) {
            sendMessage(context, "Отправьте местоположение, чтобы бот мог определить ваше заведение \n\nили \n\nвыберите заведение из списка");
            try {
                context.getBot().execute(sendKeyBoardMessage(context.getTelegramMessage().getChatId()));
            } catch (TelegramApiValidationException e) {
                e.printStackTrace();
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }

            return true;
        }

        @Override
        public void handleInput(BotContext context) {
            List<LinkedHashMap<String, String>> companies = context.getBot().getTelegramApiService().getAllCompanies().join();

            try {
                context.getBot().execute(sendInlineKeyBoardMessageListOfCompanies(context.getTelegramMessage().getChatId(), companies));
            } catch (TelegramApiException e) {
                e.getMessage();
            }
        }

        @Override
        public void handleInput(BotContext context, LocationDto locationDto) throws ExecutionException, InterruptedException {
            List<LinkedHashMap<String, String>> companies = context.getBot().getTelegramApiService().sendGeoLocation(locationDto).join();

            if (companies.isEmpty()) {
                sendMessage(context, "Не удалось получить геоданные. Попробуйте выбрать заведение из списка вручную.");
                companies = context.getBot().getTelegramApiService().getAllCompanies().join();

                try {
                    context.getBot().execute(sendInlineKeyBoardMessageListOfCompanies(context.getTelegramMessage().getChatId(), companies));
                } catch (TelegramApiException e) {
                    e.getMessage();
                }

                return;
            }

            try {
                context.getBot().execute(sendInlineKeyBoardMessageListOfCompanies(context.getTelegramMessage().getChatId(), companies));
            } catch (TelegramApiException e) {
                e.getMessage();
            }
        }

        @Override
        public BotState nextState() {
            return EnterPerformerName;
        }
    },

    EnterPerformerName {
        @Override
        public boolean enter(BotContext context) {
            long chatId = context.getTelegramMessage().getChatId();
            listMap.remove(chatId);
            sendMessage(context, "Введите исполнителя");
            return true;
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
        public boolean enter(BotContext context) {
            sendMessage(context, "Введите название песни:");
            return true;
        }

        @Override
        public void handleInput(BotContext context) {
            next = GetDBSongsList;
            context.getTelegramMessage().setSongName(context.getInput());
        }

        @Override
        public BotState nextState() {
            return next;
        }

    },

    GetDBSongsList {
        private BotState next;

        @Override
        public boolean enter(BotContext context) {
            next = LoadDBSong;
            // самая важная часть - ConcurrentHashMap<long, SongsListResponse>
            // именно сюда асинхронно кладутся списки от сервера,
            // ключом выступает ИД чата
            long chatId = context.getTelegramMessage().getChatId();
            LOGGER.info("ChatID = {}", chatId);
            try {
                SongsListResponse list = listMap.get(chatId);
                if (list == null) {
                    SongRequest request = new SongRequest(context.getTelegramMessage());
                    SongsListResponse tmp = context.getBot().getTelegramApiService().databaseSearch(request).join();
                    listMap.put(chatId, tmp);
                    list = listMap.get(chatId);
                }

                if (list.getSongs().isEmpty()) {
                    sendMessage(context, "Песня не найдена в БД.");
                    next = SearchSongByServices;
                    return false;
                }

                context.getBot().execute(sendInlineKeyBoardMessageListOfSongs(chatId, list));
                return true;

            } catch (Exception ex) {
                ex.printStackTrace();
                sendMessage(context, "Песня не найдена в БД.");
                next = SearchSongByServices;
                return false;
            }
        }

        @Override
        public BotState nextState() {
            return next;
        }
    },

    LoadDBSong {
        private BotState next;

        @Override
        public boolean enter(BotContext context) {
            next = ApproveSong;
            sendMessage(context, "Песня загружается...");
            sendAnimation(context, "https://media.giphy.com/media/QCJvAY0aFxZgPn1Ok1/giphy.gif", 20, 20);
            sendAction(context, ActionType.UPLOADAUDIO);
            // самая важная часть - ConcurrentHashMap<long, SongResponse>
            // именно сюда асинхронно кладутся треки-ответы от сервера,
            // ключом выступает ИД чата
            try {
                long chatId = context.getTelegramMessage().getChatId();
                LOGGER.info("ChatID = {}", chatId);
                SongRequest request = new SongRequest(context.getTelegramMessage());
                SongResponse temp = context.getBot().getTelegramApiService().loadSong(request).get();
                map.put(chatId, temp);
                SongResponse songResponse = map.get(chatId);

                processSong(context, songResponse);

                return false;
            } catch (Exception ex) {
                ex.printStackTrace();
                sendMessage(context, "Ошибка при загрузке трека");
                next = EnterPerformerName;

                return false;
            }
        }

        @Override
        public BotState nextState() {
            return next;
        }
    },

    SearchSongByServices() {
        private BotState next;

        @Override
        public boolean enter(BotContext context) {
            long chatId = context.getTelegramMessage().getChatId();
            listMap.remove(chatId);
            sendMessage(context, "Продолжаем поиск на муз.сервисах");

            next = ApproveSong;
            sendAnimation(context, "https://media.giphy.com/media/QCJvAY0aFxZgPn1Ok1/giphy.gif", 20, 20);
            sendAction(context, ActionType.UPLOADAUDIO);
            // самая важная часть - ConcurrentHashMap<long, SongResponse>
            // именно сюда асинхронно кладутся треки-ответы от сервера,
            // ключом выступает ИД чата
            try {
                LOGGER.info("ChatID = {}", chatId);
                SongRequest request = new SongRequest(context.getTelegramMessage());
                ResponseEntity<SongResponse> temp = context.getBot().getTelegramApiService().servicesSearch(request).join();
                if (!context.getTelegramMessage().isRepeat() && temp.getStatusCodeValue() == 228) {
                    String message = temp.getHeaders().get("Timer").toString();
                    sendMessage(context, String.format("Вы можете выполнить следующий поиск в " +
                            "музыкальных сервисах через %s сек.", message));
                    next = EnterPerformerName;
                    return false;
                }
                map.put(chatId, temp.getBody());
                SongResponse songResponse = map.get(chatId);

                processSong(context, songResponse);

                return false;
            } catch (Exception ex) {
                ex.printStackTrace();
                sendMessage(context, "Такая песня не найдена");
                next = EnterPerformerName;
                context.getTelegramMessage().setRepeat(false);
                return false;
            }
        }

        @Override
        public BotState nextState() {
            return next;
        }
    },

    //Юзер получил звуковой файл для того чтобы уточнить нужная ли это песня.
    //Юзер должен нажать "да" если это та песня.
    ApproveSong {
        private BotState next;

        @Override
        public boolean enter(BotContext context) {
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

            return true;
        }

        public void handleInput(BotContext context) {
            String text = context.getInput();
            long chatId = context.getTelegramMessage().getChatId();
            if (text.equals("Да")) {
                listMap.remove(chatId);
                //получаю из контекста позицию искомой песни в song_queue
                Long position = context.getTelegramMessage().getPosition();
                context.getTelegramMessage().setRepeat(false);
                if (position == 0) {
                    //TODO: fix payment
                    //next = Payment;
                    next = Approved;
                } else {
                    if (position < 11) {
                        sendMessage(context, "Эта песня уже близко =)");
                        sendMessage(context, "Она " + position + " в плейлисте!");
                    }
                    if (position > 10) {
                        sendMessage(context, "Придется немного подождать...");
                        sendMessage(context, "Эта песня " + position + " в плейлисте!");
                    }
                    next = EnterPerformerName;
                }

            } else if (text.equals("Нет")) {
                long songId = context.getTelegramMessage().getSongId();
                SongsListResponse list = listMap.get(chatId);
                if (list == null) {
                    context.getTelegramMessage().setRepeat(true);
                    next = SearchSongByServices;
                    return;
                }
                Iterator<BotSongDto> iter = list.getSongs().iterator();
                while (iter.hasNext()) {
                    BotSongDto song = iter.next();
                    if (song.getSongId().equals(songId)) {
                        iter.remove();
                    }
                }
                if (list.getSongs().isEmpty()) {
                    next = SearchSongByServices;
                } else {
                    next = GetDBSongsList;
                }
            } else {
                context.getTelegramMessage().setRepeat(false);
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
        public boolean enter(BotContext context) {
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

            return true;
        }

        @Override
        public void handleInput(BotContext context) {
            SuccessfulPayment successfulPayment = context.getSuccessfulPayment();
            if (successfulPayment != null && successfulPayment.getInvoicePayload().startsWith("pacman-player")) {
                sendMessage(context, "Спасибо за оплату");

                //DUPLICATE LINES
//                TelegramMessage telegramMessage = context.getBot().getTelegramMessageFromDB(context.getTelegramMessage().getChatId());
//                context.getBot().sendSongIdToServer(telegramMessage);
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

    Approved {
        @Override
        public boolean enter(BotContext context) {
            try {
                //DUPLICATE LINES
//                context.getBot().sendSongIdToServer(context.getTelegramMessage());
                TelegramMessage telegramMessage = context.getTelegramMessage();
                context.getBot().getTelegramApiService().addSongToQueue(telegramMessage.getSongId(), telegramMessage.getCompanyId());
                sendMessage(context, "Песня добавлена в очередь. Вы можете заказать ещё одну.");
                return false;
            } catch (Exception ex) {
                ex.printStackTrace();
                sendMessage(context, "Что-то пошло не так");
                return false;
            }
        }

        @Override
        public BotState nextState() {
            return EnterPerformerName;
        }
    };

    private static BotState[] states;

    BotState() {
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

    public static SendMessage sendInlineKeyBoardMessageListOfCompanies(long chatId, List<LinkedHashMap<String, String>> listOfCompanies) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        for (int i = 0; i < listOfCompanies.size(); i++) {
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(listOfCompanies.get(i).get("name"));
            inlineKeyboardButton.setCallbackData(String.valueOf(listOfCompanies.get(i).get("id")));

            List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
            keyboardButtonsRow.add(inlineKeyboardButton);

            rowList.add(keyboardButtonsRow);
        }

        inlineKeyboardMarkup.setKeyboard(rowList);

        SendMessage sendMessage = new SendMessage().setChatId(chatId).setText("Список заведений:").setReplyMarkup(inlineKeyboardMarkup);

        return sendMessage;
    }

    public static SendMessage sendInlineKeyBoardMessageListOfSongs(long chatId, SongsListResponse songsListResponse) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowList = new ArrayList<>();

        List<BotSongDto> songs = songsListResponse.getSongs();
        for (int i = 0; i < songs.size(); i++) {
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
            inlineKeyboardButton.setText(songs.get(i).getTrackName());
            inlineKeyboardButton.setCallbackData(String.valueOf(songs.get(i).getSongId()));

            List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
            keyboardButtonsRow.add(inlineKeyboardButton);

            rowList.add(keyboardButtonsRow);
        }

        InlineKeyboardButton escButton = new InlineKeyboardButton();
        escButton.setText("Ничего не подходит");
        escButton.setCallbackData(String.valueOf(0L));

        List<InlineKeyboardButton> keyboardButtonsRow = new ArrayList<>();
        keyboardButtonsRow.add(escButton);

        rowList.add(keyboardButtonsRow);

        inlineKeyboardMarkup.setKeyboard(rowList);

        SendMessage sendMessage = new SendMessage().setChatId(chatId)
                .setText("Песни, найденные в базе данных (выберите одну):").setReplyMarkup(inlineKeyboardMarkup);

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

    protected void processSong(BotContext context, SongResponse songResponse) {
        //в контекст передаем позицию искомой песни в очереди song_queue
        context.getTelegramMessage().setPosition(songResponse.getPosition());

        //если песня в очереди
        if (songResponse.getPosition() != 0) {
            sendMessage(context, "Эта песня уже есть в плейлисте...");
        }

        Long songId = songResponse.getSongId();
        TelegramMessage telegramMessage = context.getTelegramMessage();
        telegramMessage.setSongId(songId);
        context.getBot().saveTelegramMessage(telegramMessage);
        sendTrack(context, songResponse);
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
    protected void sendAction(BotContext context, ActionType actionType) {
        SendChatAction sendAction = new SendChatAction();
        sendAction.setAction(actionType);
        sendAction.setChatId(context.getTelegramMessage().getChatId());

        try {
            context.getBot().execute(sendAction);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    protected void sendAnimation(BotContext context, String url, int width, int height) {
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

    public void handleInput(BotContext context) throws ExecutionException, InterruptedException {
    }

    public void handleInput(BotContext context, Update update) {
    }

    public void handleInput(BotContext context, LocationDto locationDto) throws ExecutionException, InterruptedException {
    }

    public abstract boolean enter(BotContext context);

    public abstract BotState nextState();

    private static final Logger LOGGER = LoggerFactory.getLogger(BotState.class);

    private static final ConcurrentHashMap<Long, SongResponse> map = new ConcurrentHashMap<>();

    private static final ConcurrentHashMap<Long, SongsListResponse> listMap = new ConcurrentHashMap<>();
}