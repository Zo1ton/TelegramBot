import org.apache.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Main {

    private static final Logger LOG = Logger.getLogger(Bot.class);

    public static void main(String[] args) {

        //Initialize Api Context
        ApiContextInitializer.init();

        //Instantiate Telegram Bots API
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        //Register our bot
        try {
            telegramBotsApi.registerBot(new Bot());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        LOG.info("Bot is ready!");
    }
}
