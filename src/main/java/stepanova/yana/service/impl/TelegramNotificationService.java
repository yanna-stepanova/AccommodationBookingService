package stepanova.yana.service.impl;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import stepanova.yana.service.NotificationService;

@RequiredArgsConstructor
@Service
public class TelegramNotificationService extends TelegramLongPollingBot
        implements NotificationService {
    @Value("${TELEGRAM_BOT_TOKEN}")
    private final String botToken;
    @Value("${TELEGRAM_BOT_USERNAME}")
    private final String botUserName;
    @Value("${TELEGRAM_GROUP_ID}")
    private final String telegramGroupId;
    private List<Long> chatIds = new ArrayList<>(); //?

    @Override
    public void sendMessage(String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(telegramGroupId);
        sendMessage.setText(message);
        send(sendMessage);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            if (!chatIds.contains(chatId)) {
                chatIds.add(chatId);
                SendMessage answer = new SendMessage(String.valueOf(chatId),
                        String.format("Hi, %s, you are added for notification",
                                update.getMessage().getFrom()));
                send(answer);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botUserName;
    }

    protected void send(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(
                    String.format("Can't send message to chat_ID = %s ",
                            message.getChatId()), e);
        }
    }
}
