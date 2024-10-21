package stepanova.yana.service.impl;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
    private final PaymentMapper paymentMapper;
    private final StripeService stripeService;

    @Override
    @Transactional
    public PaymentDto save(Long userId, CreatePaymentRequestDto requestDto)
            throws StripeException, MalformedURLException {
        Booking bookingFromDB = bookingRepo.findByIdAndUserId(requestDto.bookingId(), userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Booking with id = %s not found for this user",
                                requestDto.bookingId())));
        Payment payment = getPaymentByBookingId(bookingFromDB.getId());
        if (!Status.PAID.equals(payment.getStatus())) {
            //payment = new Payment();
            if (payment.getId() == null) {
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
        }
        return paymentMapper.toDto(payment);
    }

    @Override
    @Transactional
    public PaymentDto getSuccess(Long userId, String sessionId) throws StripeException {
        Payment paymentFromDB = getBySessionAndUser(sessionId, userId);
        Status statusFromSession = Status.getByType(stripeService.getSession(sessionId)
                .getPaymentStatus());
        paymentFromDB.setStatus(statusFromSession);
        Booking bookingFromDB = bookingRepo.findById(paymentFromDB.getBooking().getId())
                .get();
        bookingFromDB.setStatus(statusFromSession);
        bookingRepo.save(bookingFromDB);
        return paymentMapper.toDto(paymentRepo.save(paymentFromDB));
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

    private Payment getPaymentByBookingId(Long bookingId) {
        return paymentRepo.findByBookingId(bookingId)
                .orElseGet(Payment::new);
    }

    private Payment getBySessionAndUser(String sessionId, Long userId) {
        return paymentRepo.findBySessionIdAndUserId(sessionId, userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("There isn't such payment session by id = %s for userId = %s",
                                sessionId, userId)));
    }
}
