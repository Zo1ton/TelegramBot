import org.apache.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;

/**
 * @author Andrey Vdovin
 * @version 0.0.0.0001
 */
public class Bot extends TelegramLongPollingBot {

    private static final Logger LOG = Logger.getLogger(Bot.class);
    private long count = 0;

    public static void main(String[] args) {

        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        try {
            telegramBotsApi.registerBot(new Bot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void onUpdateReceived(Update update) {

//        LOG.info("Запрос № " + count++);

        System.out.println("================"+ count++);

        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            switch (message.getText()) {
                case "/help" : sendMsg(message, "Бог в помощь!");
                break;
                default: sendMsg(message, "Its work!!!");
            }
        } else {
            System.out.println("ERROR");
        }
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
