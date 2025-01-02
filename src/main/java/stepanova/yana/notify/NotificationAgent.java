package stepanova.yana.notify;

import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import stepanova.yana.dto.accommodation.AccommodationDto;
import stepanova.yana.dto.booking.BookingDto;
import stepanova.yana.dto.payment.PaymentDto;
import stepanova.yana.exception.CustomTelegramApiException;
import stepanova.yana.util.MessageFormatter;

@Component
@RequiredArgsConstructor
public class NotificationAgent {
    private static final Logger log = LogManager.getLogger(NotificationAgent.class);
    private final TelegramNotificationService telegramNote;

    public <T> void notifyTelegramAsync(T objectDto, String action) {
        CompletableFuture.runAsync(() -> publishEvent(objectDto, action));
    }

    private <T> void publishEvent(T objectDto, String option) {
        String message = formatMessage(objectDto, option);
        try {
            telegramNote.sendMessage(message);
        } catch (CustomTelegramApiException ex) {
            try {
                log.warn("Failed to notify Telegram for {} ID {}: {}",
                        objectDto.getClass().getSimpleName(),
                        objectDto.getClass().getDeclaredField("id"),
                        ex.getMessage());
            } catch (NoSuchFieldException e) {
                log.warn("That class don't have the field: {}",e.getMessage());
            }
        }
    }

    private <T> String formatMessage(T objectDto, String action) {
        if (objectDto instanceof AccommodationDto accommodationDto) {
            return MessageFormatter.formatAccommodationMessage(accommodationDto, action);
        } else if (objectDto instanceof BookingDto bookingDto) {
            return MessageFormatter.formatBookingMessage(bookingDto, action);
        } else if (objectDto instanceof PaymentDto paymentDto) {
            return MessageFormatter.formatPaymentMessage(paymentDto, action);
        }
        return null;
    }
}
