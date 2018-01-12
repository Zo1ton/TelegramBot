import org.apache.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Andrey Vdovin
 * @version 0.0.0.0001
 */
public class Bot extends TelegramLongPollingBot {

    private static final Logger LOG = Logger.getLogger(Bot.class);
    private long count = 0;
    private Map<Integer, Player> playerList = start();

    public static void main(String[] args) {
        LOG.info("Bot is ready!");

        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        try {
            telegramBotsApi.registerBot(new Bot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void onUpdateReceived(Update update) {
        LOG.info("Запрос № " + ++count);

        Message message = update.getMessage();
        String name = message.getChat().getFirstName() + " " + message.getChat().getLastName();
        LOG.info("User id=" + message.getChat().getId() + " " + name);
        if (/*message != null && */message.hasText()) {
            LOG.info("Command - " + message.getText());
            if (message.getText().equals("/start")) {
                sendMsg(message, Tunes.startMsg.getTune());
            } else if (message.getText().equals("/help")) {
                sendMsg(message, "Бог в помощь!");
            } else if (message.getText().equals("/hello")) {
                sendMsg(message, "Привет " + name + "!");
            } else if (message.getText().equals("/yes") || message.getText().equals("Иду")) {
                vote(message.getFrom(), true);
                sendMsg(message, "Ты записан! Так держать!");
            } else if (message.getText().equals("/no") || message.getText().equals("Не иду")) {
                vote(message.getFrom(), false);
                sendMsg(message, "Ты отписан! Жаль(");
            } else if (message.getText().equals("/statistics") || message.getText().equals("Статистика")) {
                sendMsg(message, viewStatistic());
            } else if (message.getText().equals("/clearAllData")) {
                newGame();
            /*} else if (message.getText().equals("/keyboard")) {
                showKeyboard(message1);*/
            } else {
                sendMsg(message, "Нет такой команды!");
            }
        } else {
            LOG.info("Нет текст");
            sendMsg(message, "Пока что работает только с текстом!");
        }
        showKeyboard(message.getChatId());
    }

    private void showKeyboard(Long chat_id) {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();

        KeyboardRow row = new KeyboardRow();
        row.add("Иду");
        row.add("Не иду");
        keyboard.add(row);

        row = new KeyboardRow();
        row.add("Статистика");
        keyboard.add(row);

        keyboardMarkup.setKeyboard(keyboard);
        SendMessage message1 = new SendMessage();
        message1.setReplyMarkup(keyboardMarkup).setChatId(chat_id).setText("_");
        try {
            sendMessage(message1); // Sending our message object to user
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String viewStatistic() {
        StringBuilder yes = new StringBuilder();
        StringBuilder no = new StringBuilder();
        long x = 0;
        for (Player player : playerList.values()) {
            if (player.isPlay()) {
                yes.append(player.getFullName()).append("\n");
                x++;
            } else {
                no.append(player.getFullName()).append("\n");
            }
        }
        return "Идут(" + x + "):\n" + yes + "\nНе идут(" + (playerList.size() - x) + "):\n" + no;
    }

    private void vote(User user, boolean isPlay) {
        if (playerList.containsKey(user.getId())) {
            playerList.remove(user.getId());
        }
        addPlayerToMap(user, isPlay);
    }

    private void addPlayerToMap(User user, boolean isPlay) {
        Player player = new Player();
        player.setId(user.getId());
        player.setFirstName(user.getFirstName());
        player.setSecondName(user.getLastName());
        player.setPlay(isPlay);
        playerList.put(user.getId(), player);

        try (FileOutputStream fos = new FileOutputStream(Tunes.dbFile.getTune());
             ObjectOutputStream out = new ObjectOutputStream(fos)) {
            out.writeObject(playerList);
        } catch (IOException io) {
            LOG.error("IOException при попытке прочитать\\найти " + Tunes.dbFile.getTune() + " файл");
            io.printStackTrace();
        }
    }

    private void newGame() {
        playerList = new LinkedHashMap<>();
    }

    private void sendMsg(Message message, String s) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText(s);
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private Map<Integer, Player> start() {
        File file = new File(Tunes.dbFile.getTune());
        if (file.exists()) {
            try (FileInputStream fis = new FileInputStream(Tunes.dbFile.getTune());
                ObjectInputStream in = new ObjectInputStream(fis)) {
                return playerList = (Map<Integer, Player>) in.readObject();
            } catch (IOException io) {
                io.printStackTrace();
                return playerList = new LinkedHashMap<>();
            } catch (ClassNotFoundException cnfe) {
                LOG.error("ClassNotFoundException\n" + cnfe);
                return playerList = new LinkedHashMap<>();
            }
        } else {
            return playerList = new LinkedHashMap<>();
        }
    }

    public String getBotUsername() {
        return "sbtFootball";
    }

    public String getBotToken() {
        return "505770620:AAFhaoqjTxtXZnPGfgn820l9KAMpfKJIokQ";
    }
}