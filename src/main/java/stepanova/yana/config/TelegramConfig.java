package stepanova.yana.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import stepanova.yana.notify.TelegramNotificationService;

@RequiredArgsConstructor
@Configuration
public class TelegramConfig {
    private final TelegramNotificationService telegram;

    @Bean
    public TelegramBotsApi telegramBotsApi(TelegramNotificationService telegram)
            throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(telegram);
        return botsApi;
    }
}
