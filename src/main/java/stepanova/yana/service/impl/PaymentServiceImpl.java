package stepanova.yana.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.Period;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import stepanova.yana.dto.payment.CreatePaymentRequestDto;
import stepanova.yana.dto.payment.PaymentDto;
import stepanova.yana.mapper.PaymentMapper;
import stepanova.yana.model.Booking;
import stepanova.yana.model.Payment;
import stepanova.yana.model.Status;
import stepanova.yana.repository.booking.BookingRepository;
import stepanova.yana.repository.payment.PaymentRepository;
import stepanova.yana.service.PaymentService;
import stepanova.yana.service.StripeService;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepo;
    private final BookingRepository bookingRepo;
    private final StripeService stripeService;
    private final PaymentMapper paymentMapper;
    @Value("${STRIPE_PUBLIC_KEY}")
    private String stripePublicKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripePublicKey;
    }

    @Override
    public PaymentDto save(Long userId, CreatePaymentRequestDto requestDto) {
        Booking bookingFromDB = bookingRepo.findByIdAndUserId(requestDto.bookingId(), userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Booking with id = %s not found for this user",
                                requestDto.bookingId())));
        Period daysOfBooking = Period.between(
                bookingFromDB.getCheckInDate(),
                bookingFromDB.getCheckOutDate());
        BigDecimal amountToPay = bookingFromDB.getAccommodation().getDailyRate().multiply(
                BigDecimal.valueOf(daysOfBooking.getDays()));

        Session session;
        Payment payment = new Payment();
        payment.setDateTimeCreated(LocalDateTime.now());
        payment.setBooking(bookingFromDB);
        payment.setAmountToPay(amountToPay);
        try {
            session = stripeService.createSession(bookingFromDB, amountToPay);
            payment.setStatus(Status.PAID);
            payment.setSessionID(session.getId());
            payment.setSessionUrl(new URL(session.getReturnUrl()));
        } catch (StripeException e) {
            payment.setStatus(Status.PENDING);
            payment.setSessionID("");
        } catch (MalformedURLException e) {
            payment.setSessionUrl(null);
        }
        return paymentMapper.toDto(paymentRepo.save(payment));
    }
}
