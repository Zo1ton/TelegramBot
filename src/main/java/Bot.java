import org.apache.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.api.objects.User;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Andrey Vdovin
 * @version 0.0.0.0001
 */
public class Bot extends TelegramLongPollingBot {

    private static final Logger LOG = Logger.getLogger(Bot.class);
    private long count = 0;
    private Map<Integer, Player> playerList = new HashMap<>();

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
        if (message != null && message.hasText()) {
            switch (message.getText()) {
                case "/start":
                    sendMsg(message, Tunes.startMsg.getTune());
                    break;
                case "/help":
                    sendMsg(message, "Бог в помощь!");
                    break;
                case "/hello":
                    String name = message.getChat().getFirstName() + " " + message.getChat().getLastName();
                    sendMsg(message, "Привет " + name + "!");
                    break;
                case "/yes":
                    vote(message.getFrom(), true);
                    sendMsg(message, "Ты записан! Так держать!");
                    break;
                case "/no":
                    vote(message.getFrom(), false);
                    sendMsg(message, "Ты отписан! Жаль(");
                    break;
                case "/statistics":
                    sendMsg(message, viewStatistic());
                    break;
                default:
                    sendMsg(message, "Нет такой команды!");
                    sendMsg(message, "id - " + message.getFrom().toString());
                    LOG.info(message.getChat().getFirstName() + " " + message.getChat().getLastName());
            }
        } else {
            System.out.println("ERROR");
        }
    }

    private String viewStatistic() {
        StringBuilder yes = new StringBuilder();
        StringBuilder no = new StringBuilder();
        for (Player player : playerList.values()) {
            if (player.isPlay()) {
                yes.append(player.getFullName()).append("\n");
            } else {
                no.append(player.getFullName()).append("\n");
            }
        }
        return "Идут:\n" + yes + "\nНе идут:\n" + no;
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

    public String getBotUsername() {
        return "sbtFootball";
    }

    public String getBotToken() {
        return "505770620:AAFhaoqjTxtXZnPGfgn820l9KAMpfKJIokQ";
    }
}