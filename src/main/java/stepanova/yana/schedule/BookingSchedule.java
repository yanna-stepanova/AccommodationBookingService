package stepanova.yana.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import stepanova.yana.notify.NotificationAgent;
import stepanova.yana.service.BookingService;

@RequiredArgsConstructor
@Component
public class BookingSchedule {
    private final BookingService bookingService;
    private final NotificationAgent notificationAgent;

    @Scheduled(cron = "0 0 13 * * *", zone = "Europe/Kiev")
    public void sendNotification() {
        bookingService.expiredBookings().forEach(
                bookingDto -> notificationAgent.notifyTelegramAsync(bookingDto, "Expired"));
    }
}
