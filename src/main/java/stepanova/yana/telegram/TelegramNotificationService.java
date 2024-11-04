package stepanova.yana.telegram;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Service
public class TelegramNotificationService extends TelegramLongPollingBot
        implements NotificationService {
    @Value("${telegram.bot.username}")
    private String botUsername;
    @Value("${telegram.group.id}")
    private String groupId;
    private List<Long> chatIds = new ArrayList<>();

    public TelegramNotificationService(@Value("${telegram.bot.token}") String botToken) {
        super(botToken);
    }

    @Override
    public void sendMessage(String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(groupId);
        sendMessage.setText(message);
        send(sendMessage);
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            if (!chatIds.contains(chatId)) {
                chatIds.add(chatId);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    protected void send(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException("Can't send message to chat " + message.getChatId(), e);
        }
    }
}
