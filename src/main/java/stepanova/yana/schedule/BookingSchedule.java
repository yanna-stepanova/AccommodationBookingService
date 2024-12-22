package stepanova.yana.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import stepanova.yana.service.BookingService;

@RequiredArgsConstructor
@Component
public class BookingSchedule {
    private final BookingService bookingService;

    @Scheduled(cron = "0 0 13 * * *", zone = "Europe/Kiev")
    public void sendNotification() {
        bookingService.expiredBookings();
    }
}
