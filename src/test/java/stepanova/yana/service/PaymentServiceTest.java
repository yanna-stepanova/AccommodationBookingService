package stepanova.yana.service;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import stepanova.yana.dto.accommodation.AccommodationDtoWithoutAvailability;
import stepanova.yana.dto.payment.CreatePaymentRequestDto;
import stepanova.yana.dto.payment.PaymentDto;
import stepanova.yana.mapper.PaymentMapper;
import stepanova.yana.model.Accommodation;
import stepanova.yana.model.Booking;
import stepanova.yana.model.Payment;
import stepanova.yana.model.Status;
import stepanova.yana.model.User;
import stepanova.yana.notify.TelegramNotificationService;
import stepanova.yana.repository.BookingRepository;
import stepanova.yana.repository.PaymentRepository;
import stepanova.yana.service.impl.PaymentServiceImpl;
import stepanova.yana.util.DataFactoryForServices;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {
    @Mock
    private PaymentRepository paymentRepo;
    @Mock
    private BookingRepository bookingRepo;
    @Mock
    private PaymentMapper paymentMapper;
    @Mock
    private StripeService stripeService;
    @Mock
    private TelegramNotificationService telegramService;
    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Test
    @DisplayName("Get correct PaymentDto for valid user id and requestDto")
    void save_WithValidUserAndRequestDto_ReturnPaymentDto()
            throws StripeException, MalformedURLException {
        //Given
        Long userId = 2L;
        CreatePaymentRequestDto requestDto = new CreatePaymentRequestDto(3L);

        User user = new User();
        user.setId(userId);
        Accommodation accommodation = new Accommodation(8L);
        accommodation.setDailyRate(BigDecimal.valueOf(4L));
        Booking booking = new Booking();
        booking.setId(requestDto.bookingId());
        booking.setCheckInDate(LocalDate.now().minusDays(1L));
        booking.setCheckOutDate(LocalDate.now().plusDays(1L));
        booking.setUser(user);
        booking.setAccommodation(accommodation);
        Session session = DataFactoryForServices.createValidSession();
        Payment payment = new Payment();
        payment.setSessionID(session.getId());
        payment.setSessionUrl(new URL(session.getUrl()));

        PaymentDto expected = new PaymentDto(1L, Status.PENDING,
                LocalDateTime.of(LocalDate.now(), LocalTime.now()),
                requestDto.bookingId(),
                new AccommodationDtoWithoutAvailability(),
                null, payment.getSessionUrl().toString(), session.getId());
        Mockito.when(bookingRepo.findByIdAndUserId(requestDto.bookingId(), userId))
                .thenReturn(Optional.of(booking));
        Mockito.when(paymentRepo.findByBookingId(requestDto.bookingId()))
                .thenReturn(Optional.of(payment));
        Mockito.when(stripeService.createPaymentSession(
                Mockito.eq(booking), Mockito.any(BigDecimal.class))).thenReturn(session);
        Mockito.when(paymentRepo.save(payment)).thenReturn(payment);
        Mockito.when(paymentMapper.toDto(payment)).thenReturn(expected);

        //When
        PaymentDto actual = paymentService.save(userId, requestDto);

        //Then
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @Test
    @DisplayName("Exception: if get PaymentDto for non-valid user id and requestDto")
    void save_WithNonValidUserAndRequestDto_ReturnException() {
        //Given
        Long userId = 2L;
        CreatePaymentRequestDto requestDto = new CreatePaymentRequestDto(100L);

        Mockito.when(bookingRepo.findByIdAndUserId(requestDto.bookingId(), userId))
                .thenReturn(Optional.empty());

        //When
        Exception exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> paymentService.save(userId, requestDto));

        //Then
        String expected = String.format("Booking with id = %s not found for this user",
                requestDto.bookingId());
        String actual = exception.getMessage();
        Assertions.assertEquals(expected, actual);
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @Test
    @DisplayName("Get correct PaymentDto for valid user id and session id")
    void getSuccess_WithValidUserIdAndSessionId_ReturnPaymentDto()
            throws StripeException {
        //Given
        String sessionId = "cs_test_a11G0L";
        Session session = new Session();
        session.setId(sessionId);
        session.setPaymentStatus("paid");

        Booking booking = new Booking();
        booking.setId(32L);
        booking.setAccommodation(new Accommodation(4L));

        Payment payment = new Payment();
        payment.setSessionID(sessionId);
        payment.setBooking(booking);

        Long userId = 6L;
        PaymentDto expected = new PaymentDto(78L, null, null, null,
                new AccommodationDtoWithoutAvailability(), null, null, sessionId);
        Mockito.when(paymentRepo.findBySessionIdAndUserId(sessionId, userId))
                .thenReturn(Optional.of(payment));
        Mockito.when(stripeService.getSession(sessionId)).thenReturn(session);
        Mockito.when(bookingRepo.findById(Mockito.anyLong())).thenReturn(Optional.of(booking));
        Mockito.when(bookingRepo.save(booking)).thenReturn(booking);
        Mockito.when(paymentRepo.save(payment)).thenReturn(payment);
        Mockito.when(paymentMapper.toDto(payment)).thenReturn(expected);

        //When
        PaymentDto actual = paymentService.getSuccess(userId, sessionId);

        //Then
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @Test
    @DisplayName("Get info-message for valid user id and session id")
    void getCancel_WithValidUserIdAndSessionId_ReturnString() {
        //Given
        Long userId = 1L;
        String sessionId = "cs_test_a234";

        Payment payment = new Payment();
        payment.setSessionID(sessionId);

        Mockito.when(paymentRepo.findBySessionIdAndUserId(sessionId, userId))
                .thenReturn(Optional.of(payment));

        //When
        String expected = String.format("Payment for sessionId = %s can be made later"
                + " (but the session is available for only 24 hours)", sessionId);
        String actual = paymentService.getCancel(userId, sessionId);

        //Then
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Exception: if get cancel url for non-valid user id and session id")
    void getCancel_WithNonValidUserIdAndSessionId_ReturnException() {
        //Given
        Long userId = 12L;
        String sessionId = "cs_test_a178";

        Mockito.when(paymentRepo.findBySessionIdAndUserId(sessionId, userId))
                .thenReturn(Optional.empty());

        //When
        Exception exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> paymentService.getCancel(userId, sessionId));

        //Then
        String expected = String.format(
                "There isn't such payment session by id = %s for userId = %s",
                sessionId, userId);
        String actual = exception.getMessage();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get list of one PaymentDto for valid user id")
    void getAllByUser_WithValidUserId_ReturnOne() {
        //Given
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        Booking booking = new Booking();
        booking.setId(9L);
        booking.setUser(user);
        booking.setAccommodation(new Accommodation(12L));

        Payment payment = new Payment();
        payment.setId(17L);
        payment.setBooking(booking);

        PaymentDto paymentDto = new PaymentDto(payment.getId(), null, null,
                payment.getBooking().getId(),
                new AccommodationDtoWithoutAvailability(), null, null, null);

        List<Payment> paymentList = List.of(payment);

        Mockito.when(paymentRepo.findAllByUserId(userId)).thenReturn(paymentList);
        Mockito.when(paymentMapper.toDto(payment)).thenReturn(paymentDto);

        //When
        List<PaymentDto> expected = List.of(paymentDto);
        List<PaymentDto> actual = paymentService.getAllByUser(userId);

        //Then
        Assertions.assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            Assertions.assertTrue(
                    EqualsBuilder.reflectionEquals(expected.get(i), actual.get(i)));
        }
    }
}
