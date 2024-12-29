package stepanova.yana.service;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
import stepanova.yana.dto.booking.BookingDto;
import stepanova.yana.dto.booking.BookingDtoWithoutDetails;
import stepanova.yana.dto.booking.CreateBookingRequestDto;
import stepanova.yana.dto.booking.UpdateBookingStatusRequestDto;
import stepanova.yana.dto.location.LocationDto;
import stepanova.yana.dto.user.UserResponseDto;
import stepanova.yana.mapper.BookingMapper;
import stepanova.yana.model.Accommodation;
import stepanova.yana.model.Booking;
import stepanova.yana.model.Role;
import stepanova.yana.model.Status;
import stepanova.yana.model.User;
import stepanova.yana.repository.AccommodationRepository;
import stepanova.yana.repository.BookingRepository;
import stepanova.yana.repository.PaymentRepository;
import stepanova.yana.service.impl.BookingServiceImpl;
import stepanova.yana.telegram.TelegramNotificationService;
import stepanova.yana.util.DataFactoryForServices;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {
    @Mock
    private BookingRepository bookingRepo;
    @Mock
    private PaymentRepository paymentRepo;
    @Mock
    private AccommodationRepository accommodationRepo;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private TelegramNotificationService telegramService;
    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    @DisplayName("Get correct BookingDto for valid requestDto")
    void save_WithValidUserAndRequestDto_ReturnBookingDto() {
        //Given
        Role role = DataFactoryForServices.createValidCustomerRole();
        User user = DataFactoryForServices.createValidUser(role);

        CreateBookingRequestDto requestDto = DataFactoryForServices.createValidBookingRequestDto();
        Accommodation accommodation = new Accommodation(requestDto.accommodationId());
        accommodation.setAvailability(3);
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setStatus(Status.PENDING);
        booking.setAccommodation(accommodation);
        booking.setCheckInDate(requestDto.checkInDate());
        booking.setCheckOutDate(requestDto.checkOutDate());

        BookingDto expected = new BookingDto(2L,
                booking.getCheckInDate(), booking.getCheckOutDate(),
                new AccommodationDtoWithoutAvailability(accommodation.getId(),
                        accommodation.getType(), new LocationDto(),
                        accommodation.getSize(), Set.of(),
                        accommodation.getDailyRate()),
                new UserResponseDto(user.getId(), user.getEmail(), user.getFirstName(),
                        user.getLastName(), user.getRole().getName().getRoleName()),
                booking.getStatus());

        Mockito.when(paymentRepo.findAllByUserId(user.getId())).thenReturn(List.of());
        Mockito.when(bookingMapper.toModel(requestDto)).thenReturn(booking);
        Mockito.when(accommodationRepo.findById(booking.getAccommodation().getId()))
                .thenReturn(Optional.of(accommodation));
        Mockito.when(bookingRepo.findAllByAccommodationIdAndStatusAndFromDateAndToDate(
                booking.getAccommodation().getId(),
                Status.CANCELED.getStatusName(),
                booking.getCheckInDate(),
                booking.getCheckOutDate())).thenReturn(List.of());
        Mockito.when(bookingRepo.save(booking)).thenReturn(booking);
        Mockito.when(bookingMapper.toDto(booking)).thenReturn(expected);

        //When
        BookingDto actual = bookingService.save(user, requestDto);

        //Then
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @Test
    @DisplayName("Get a list with one of BookingDtoWithoutDetails")
    void getAllByUserAndStatus_WithValidUserIdAndStatus_ReturnOne() {
        //Given
        Long userId = 2L;
        User user = new User();
        user.setId(userId);

        String statusName = "paid";
        Status status = Status.valueOf(statusName.toUpperCase());

        Booking booking = DataFactoryForServices.createValidBooking(user, status);

        BookingDtoWithoutDetails bookingDto = new BookingDtoWithoutDetails(booking.getId(),
                booking.getCheckInDate(), booking.getCheckOutDate(),
                booking.getAccommodation().getId(), userId, status);

        List<BookingDtoWithoutDetails> expected = List.of(bookingDto);

        Mockito.when(bookingRepo.findAllByUserIdAndStatus(userId, status))
                .thenReturn(List.of(booking));
        Mockito.when(bookingMapper.toDtoWithoutDetails(booking)).thenReturn(bookingDto);

        //When
        List<BookingDtoWithoutDetails> actual = bookingService.getAllByUserAndStatus(
                userId, statusName);

        //Then
        Assertions.assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            Assertions.assertTrue(
                    EqualsBuilder.reflectionEquals(expected.get(i), actual.get(i)));
        }
    }

    @Test
    @DisplayName("Get correct BookingDto for valid booking id and user id")
    void getBookingByIdAndUserId_WithValidIdAndUserId_ReturnBookingDto() {
        //Given
        Long bookingId = 6L;
        Long userId = 3L;
        User user = new User();
        user.setId(userId);
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setUser(user);

        BookingDto expected = new BookingDto(bookingId, booking.getCheckInDate(),
                booking.getCheckOutDate(), new AccommodationDtoWithoutAvailability(),
                new UserResponseDto(), booking.getStatus());

        Mockito.when(bookingRepo.findByIdAndUserId(bookingId, userId))
                .thenReturn(Optional.of(booking));
        Mockito.when(bookingMapper.toDto(booking)).thenReturn(expected);

        //When
        BookingDto actual = bookingService.getBookingByIdAndUserId(userId, bookingId);

        //Then
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @Test
    @DisplayName("Get list of all BookingDtoWithoutDetails for valid user id")
    void getAllByUser_WithValidUserId_ReturnTwo() {
        //Given
        Long userId = 5L;
        User user = new User();
        user.setId(userId);

        Booking bookingOne = new Booking();
        bookingOne.setId(23L);
        bookingOne.setUser(user);
        bookingOne.setAccommodation(new Accommodation(13L));

        Booking bookingTwo = new Booking();
        bookingTwo.setId(32L);
        bookingTwo.setUser(user);
        bookingTwo.setAccommodation(new Accommodation(31L));
        List<Booking> listOfBooking = List.of(bookingOne, bookingTwo);

        BookingDtoWithoutDetails bookingDtoOne = new BookingDtoWithoutDetails(bookingOne.getId(),
                bookingOne.getCheckInDate(), bookingOne.getCheckOutDate(),
                bookingOne.getAccommodation().getId(),
                bookingOne.getUser().getId(), bookingOne.getStatus());
        BookingDtoWithoutDetails bookingDtoTwo = new BookingDtoWithoutDetails(bookingTwo.getId(),
                bookingTwo.getCheckInDate(), bookingTwo.getCheckOutDate(),
                bookingTwo.getAccommodation().getId(),
                bookingTwo.getUser().getId(), bookingTwo.getStatus());

        Mockito.when(bookingRepo.findAllByUserId(Mockito.anyLong())).thenReturn(listOfBooking);
        Mockito.when(bookingMapper.toDtoWithoutDetails(bookingOne)).thenReturn(bookingDtoOne);
        Mockito.when(bookingMapper.toDtoWithoutDetails(bookingTwo)).thenReturn(bookingDtoTwo);

        //When
        List<BookingDtoWithoutDetails> expected = List.of(bookingDtoOne, bookingDtoTwo);
        List<BookingDtoWithoutDetails> actual = bookingService.getAllByUser(userId);

        //Then
        Assertions.assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            Assertions.assertTrue(
                    EqualsBuilder.reflectionEquals(expected.get(i), actual.get(i)));
        }
    }

    @Test
    @DisplayName("Get correct BookingDto for existing booking id")
    void getBookingById_WithExistingBookingId_ReturnBookingDto() {
        //Given
        Long bookingId = 10L;
        Booking booking = new Booking();
        booking.setId(bookingId);

        BookingDto expected = new BookingDto(booking.getId(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                new AccommodationDtoWithoutAvailability(),
                new UserResponseDto(),
                booking.getStatus());
        Mockito.when(bookingRepo.findById(bookingId)).thenReturn(Optional.of(booking));
        Mockito.when(bookingMapper.toDto(booking)).thenReturn(expected);

        //When
        BookingDto actual = bookingService.getBookingById(bookingId);

        //Then
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @Test
    @DisplayName("Exception: if get BookingDto for non-existing booking id")
    void getBookingById_WithNonExistingBookingId_ReturnException() {
        //Given
        Long bookingId = 11L;
        Mockito.when(bookingRepo.findById(bookingId)).thenReturn(Optional.empty());

        //When
        Exception exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> bookingService.getBookingById(bookingId));

        //Then
        String expected = String.format("Booking with id = %s not found", bookingId);
        String actual = exception.getMessage();
        Assertions.assertEquals(expected, actual);
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @Test
    @DisplayName("Get updated BookingDto by existing booking id and valid requestDto")
    void updateBookingById_WithExistingIdAndValidRequestDto_ReturnBookingDto() {
        //Given
        Long bookingId = 8L;

        User user = new User();
        user.setId(6L);
        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setAccommodation(new Accommodation(4L));
        booking.setUser(user);
        UpdateBookingStatusRequestDto requestDto = new UpdateBookingStatusRequestDto(
                Status.PAID.getStatusName());
        BookingDto expected = new BookingDto(2L,
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                new AccommodationDtoWithoutAvailability(),
                new UserResponseDto(), Status.valueOf(requestDto.statusName().toUpperCase()));
        Mockito.when(bookingRepo.findById(bookingId)).thenReturn(Optional.of(booking));
        Mockito.when(bookingMapper.updateBookingFromDto(booking, requestDto)).thenReturn(booking);
        Mockito.when(bookingRepo.save(booking)).thenReturn(booking);
        Mockito.when(bookingMapper.toDto(booking)).thenReturn(expected);

        //When
        BookingDto actual = bookingService.updateBookingById(bookingId, requestDto);

        //Then
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @Test
    @DisplayName("Exception: if get updated BookingDto by non-existing booking id")
    void updateBookingById_WithNonExistingIdAndValidRequestDto_ReturnException() {
        //Given
        Long bookingId = 9L;
        UpdateBookingStatusRequestDto requestDto = new UpdateBookingStatusRequestDto(
                Status.PAID.getStatusName());

        Mockito.when(bookingRepo.findById(bookingId)).thenReturn(Optional.empty());

        //When
        Exception exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> bookingService.updateBookingById(bookingId, requestDto));

        //Then
        String expected = String.format("Booking with id = %s not found", bookingId);
        String actual = exception.getMessage();
        Assertions.assertEquals(expected, actual);
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @Test
    @DisplayName("Get BookingDto for canceled booking by id")
    void cancelBookingById_WithValidBookingIdAndUserId_ReturnBookingDto() {
        //Given
        Long bookingId = 7L;
        Long userId = 1L;
        User user = new User();
        user.setId(userId);

        Booking booking = new Booking();
        booking.setId(bookingId);
        booking.setUser(user);
        booking.setAccommodation(new Accommodation());

        BookingDto expected = new BookingDto(bookingId,
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                new AccommodationDtoWithoutAvailability(),
                new UserResponseDto(),
                Status.CANCELED);
        Mockito.when(bookingRepo.findByIdAndUserId(bookingId, userId))
                .thenReturn(Optional.of(booking));
        Mockito.when(bookingRepo.save(booking)).thenReturn(booking);
        Mockito.when(bookingMapper.toDto(booking)).thenReturn(expected);

        //When
        BookingDto actual = bookingService.cancelBookingById(userId, bookingId);

        //Then
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @Test
    @DisplayName("Exception: if get BookingDto for canceled booking by non valid id")
    void cancelBookingById_WithNonValidBookingIdAndUserId_ReturnException() {
        //Given
        Long bookingId = 9L;
        Long userId = 19L;

        Mockito.when(bookingRepo.findByIdAndUserId(bookingId, userId)).thenReturn(Optional.empty());

        //When
        Exception exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> bookingService.cancelBookingById(userId, bookingId));

        //Then
        String expected = String.format("Booking with id = %s not found for this user", bookingId);
        String actual = exception.getMessage();
        Assertions.assertEquals(expected, actual);
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }
}
