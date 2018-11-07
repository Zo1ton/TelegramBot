import org.apache.log4j.Logger;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

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

    public void onUpdateReceived(Update update) {
        LOG.info("Запрос № " + ++count);

        Message message = update.getMessage();
        String name = message.getChat().getFirstName() + " " + message.getChat().getLastName();
        String chatId = message.getChatId().toString();
        LOG.info("User id=" + message.getChat().getId() + " " + name);
        if (message.hasText()) {
            LOG.info("Command - " + message.getText());
            if (message.getText().equals("/start")) {
                sendMsg(chatId, Tunes.startMsg.getTune());
            } else if (message.getText().equals("Иду")) {
                vote(message.getFrom(), true);
                sendMsg(chatId, "Ты записан! Так держать!");
            } else if (message.getText().equals("Не иду")) {
                vote(message.getFrom(), false);
                sendMsg(chatId, "Ты отписан! Жаль(");
            } else if (message.getText().equals("Статистика")) {
                sendMsg(chatId, viewStatistic());
            } else if (message.getText().equals("/clearAllData")) {
                newGame();
            } else {
                sendMsg(chatId, "Нет такой команды!");
            }
        } else {
            LOG.info("Нет текст");
            sendMsg(chatId, "Пока что работает только с текстом!");
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
        return String.format("Идут(%d):\n" +
                "%s\n" +
                "Не идут(%d):\n" +
                "%s", x, yes, (playerList.size() - x), no);
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

    private void sendMsg(String chatId, String messageText) {
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

        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setReplyMarkup(keyboardMarkup).setText(messageText);
        try {
            execute(sendMessage);
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