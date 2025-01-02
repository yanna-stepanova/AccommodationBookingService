package stepanova.yana.notify;

import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import stepanova.yana.dto.accommodation.AccommodationDto;
import stepanova.yana.exception.CustomTelegramApiException;
import stepanova.yana.util.MessageFormatter;

@Component
@RequiredArgsConstructor
public class NotificationAgent {
    private static final Logger log = LogManager.getLogger(NotificationAgent.class);
    private final TelegramNotificationService telegramNote;

    public void notifyTelegramAsync(AccommodationDto accommodationDto, String action) {
        CompletableFuture.runAsync(() -> publishEvent(accommodationDto, action));
    }

    private void publishEvent(AccommodationDto accommodationDto, String option) {
        String message = MessageFormatter.formatAccommodationMessage(accommodationDto, option);
        try {
            telegramNote.sendMessage(message);
        } catch (CustomTelegramApiException ex) {
            log.warn("Failed to notify Telegram for accommodation ID {}: {}",
                    accommodationDto.getId(), ex.getMessage());
        }
    }
}
