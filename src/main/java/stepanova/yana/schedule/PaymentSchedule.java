package stepanova.yana.schedule;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import stepanova.yana.service.PaymentService;

@RequiredArgsConstructor
@Component
public class PaymentSchedule {
    private final PaymentService paymentService;

    @Scheduled(cron = "0 */1 * * * *", zone = "Europe/Kiev")
    public void sendNotification() {
        paymentService.expiredPayments();
    }
}
