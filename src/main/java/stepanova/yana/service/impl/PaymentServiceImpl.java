package stepanova.yana.service.impl;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import stepanova.yana.dto.payment.CreatePaymentRequestDto;
import stepanova.yana.dto.payment.PaymentDto;
import stepanova.yana.exception.EntityNotFoundCustomException;
import stepanova.yana.mapper.PaymentMapper;
import stepanova.yana.model.Booking;
import stepanova.yana.model.Payment;
import stepanova.yana.model.Status;
import stepanova.yana.repository.BookingRepository;
import stepanova.yana.repository.PaymentRepository;
import stepanova.yana.service.PaymentService;
import stepanova.yana.service.StripeService;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {
    private static final Logger log = LogManager.getLogger(PaymentServiceImpl.class);
    private final PaymentRepository paymentRepo;
    private final BookingRepository bookingRepo;
    private final PaymentMapper paymentMapper;
    private final StripeService stripeService;
    //private final TelegramNotificationService telegramNote;

    @Override
    @Transactional
    public PaymentDto save(Long userId, CreatePaymentRequestDto requestDto)
            throws StripeException, MalformedURLException {
        Booking bookingFromDB = bookingRepo.findByIdAndUserId(requestDto.bookingId(), userId)
                .orElseThrow(() -> new EntityNotFoundCustomException(
                        String.format("Booking with id = %s not found for this user",
                                requestDto.bookingId())));
        Payment payment = getPaymentByBookingId(bookingFromDB.getId());
        if (!Status.PAID.equals(payment.getStatus()) && (payment.getId() == null)) {
            Period daysOfBooking = Period.between(
                    bookingFromDB.getCheckInDate(),
                    bookingFromDB.getCheckOutDate());
            BigDecimal amountToPay = bookingFromDB.getAccommodation().getDailyRate().multiply(
                    BigDecimal.valueOf(daysOfBooking.getDays()));
            Session paymentSession = stripeService.createPaymentSession(bookingFromDB,
                    amountToPay);
            payment.setDateTimeCreated(LocalDateTime.now());
            payment.setBooking(bookingFromDB);
            payment.setAmountToPay(amountToPay);
            payment.setStatus(Status.PENDING);
            payment.setSessionID(paymentSession.getId());
            payment.setSessionUrl(new URL(paymentSession.getUrl()));
            payment = paymentRepo.save(payment);
        }
        return paymentMapper.toDto(payment);
    }

    @Override
    @Transactional
    public PaymentDto getSuccess(Long userId, String sessionId) throws StripeException {
        Payment paymentFromDB = getBySessionAndUser(sessionId, userId);
        Status statusFromSession = Status.valueOf(stripeService.getSession(sessionId)
                .getPaymentStatus().toUpperCase());
        paymentFromDB.setStatus(statusFromSession);
        Booking bookingFromDB = bookingRepo.findById(paymentFromDB.getBooking().getId())
                .get();
        bookingFromDB.setStatus(statusFromSession);
        bookingRepo.save(bookingFromDB);
        Payment savedPayment = paymentRepo.save(paymentFromDB);
        //notifyTelegramAsync(savedPayment, "Successful");
        return paymentMapper.toDto(savedPayment);
    }

    @Override
    public String getCancel(Long userId, String sessionId) {
        if (getBySessionAndUser(sessionId, userId) == null) {
            return "";
        }
        return String.format("Payment for sessionId = %s can be made later"
                + " (but the session is available for only 24 hours)", sessionId);
    }

    @Override
    @Transactional
    public List<PaymentDto> getAllByUser(Long userId) {
        return paymentRepo.findAllByUserId(userId).stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public void expiredPayments() {
        /*LocalDateTime startDate = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0, 0));
        LocalDateTime endDate = LocalDateTime.of(LocalDate.now(), LocalTime.of(23, 59, 59));
        List<Payment> paymentList = paymentRepo.findAllByStatusNotInAndDateBetween(
                Set.of(Status.CANCELED.getStatusName(), Status.PAID.getStatusName()),
                startDate, endDate);
        if (!paymentList.isEmpty()) {
            paymentList.forEach(payment -> payment.setStatus(Status.EXPIRED));
            paymentRepo.saveAll(paymentList)
                    .forEach(payment -> publishEvent(payment, "Expired"));
        }*/
    }

    private Payment getPaymentByBookingId(Long bookingId) {
        return paymentRepo.findByBookingId(bookingId)
                .orElseGet(Payment::new);
    }

    private Payment getBySessionAndUser(String sessionId, Long userId) {
        return paymentRepo.findBySessionIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new EntityNotFoundCustomException(
                        String.format("There isn't such payment session by id = %s for userId = %s",
                                sessionId, userId)));
    }
}
