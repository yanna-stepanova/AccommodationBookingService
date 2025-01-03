package stepanova.yana.service.impl;

import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import stepanova.yana.dto.booking.BookingDto;
import stepanova.yana.dto.booking.BookingDtoWithoutDetails;
import stepanova.yana.dto.booking.CreateBookingRequestDto;
import stepanova.yana.dto.booking.UpdateBookingStatusRequestDto;
import stepanova.yana.exception.EntityNotFoundCustomException;
import stepanova.yana.mapper.BookingMapper;
import stepanova.yana.model.Accommodation;
import stepanova.yana.model.Booking;
import stepanova.yana.model.Payment;
import stepanova.yana.model.Status;
import stepanova.yana.model.User;
import stepanova.yana.repository.AccommodationRepository;
import stepanova.yana.repository.BookingRepository;
import stepanova.yana.repository.PaymentRepository;
import stepanova.yana.service.BookingService;

@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepo;
    private final BookingMapper bookingMapper;
    private final AccommodationRepository accommodationRepo;
    private final PaymentRepository paymentRepo;

    @Override
    @Transactional
    public BookingDto save(User user, CreateBookingRequestDto requestDto) {
        List<Payment> paymentsByUser = paymentRepo.findAllByUserId(user.getId());
        if (!paymentsByUser.stream()
                .filter(payment -> payment.getStatus().equals(Status.PENDING))
                .toList().isEmpty()) {
            return bookingMapper.toDto(new Booking());
        }
        Booking booking = bookingMapper.toModel(requestDto);
        Accommodation accommodationFromDB = getAccommodationById(
                booking.getAccommodation().getId());
        booking.setAccommodation(accommodationFromDB);
        booking.setUser(user);
        booking.setStatus(Status.PENDING);
        List<Booking> othersBooking = bookingRepo
                .findAllByAccommodationIdAndStatusAndFromDateAndToDate(
                        booking.getAccommodation().getId(),
                        Status.CANCELED.getStatusName(),
                        booking.getCheckInDate(),
                        booking.getCheckOutDate());
        if (othersBooking.size() >= accommodationFromDB.getAvailability()) {
            return bookingMapper.toDto(new Booking());
        }
        Booking savedBooking = bookingRepo.save(booking);
        return bookingMapper.toDto(savedBooking);
    }

    @Override
    public List<BookingDtoWithoutDetails> getAllByUserAndStatus(Long userId, String statusName) {
        return bookingRepo.findAllByUserIdAndStatus(userId,
                        Status.valueOf(statusName.toUpperCase())).stream()
                .map(bookingMapper::toDtoWithoutDetails)
                .toList();
    }

    @Override
    @Transactional
    public BookingDto getBookingByIdAndUserId(Long userId, Long bookingId) {
        return bookingRepo.findByIdAndUserId(bookingId, userId)
                .map(bookingMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundCustomException(
                        String.format("Booking with id = %s not found for this user", bookingId)));
    }

    @Override
    public List<BookingDtoWithoutDetails> getAllByUser(Long userId) {
        return bookingRepo.findAllByUserId(userId).stream()
                .map(bookingMapper::toDtoWithoutDetails)
                .toList();
    }

    @Override
    @Transactional
    public BookingDto getBookingById(Long bookingId) {
        return bookingRepo.findById(bookingId)
                .map(bookingMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundCustomException(
                        String.format("Booking with id = %s not found", bookingId)));
    }

    @Override
    @Transactional
    public BookingDto updateBookingById(Long bookingId, UpdateBookingStatusRequestDto requestDto) {
        Booking bookingFromDB = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new EntityNotFoundCustomException(
                        String.format("Booking with id = %s not found", bookingId)));
        Booking savedBooking = bookingRepo.save(bookingMapper.updateBookingFromDto(
                bookingFromDB, requestDto));
        return bookingMapper.toDto(savedBooking);
    }

    @Override
    @Transactional
    public BookingDto cancelBookingById(Long userId, Long bookingId) {
        Booking bookingFromDB = bookingRepo.findByIdAndUserId(bookingId, userId)
                .orElseThrow(() -> new EntityNotFoundCustomException(
                        String.format("Booking with id = %s not found for this user", bookingId)));
        bookingFromDB.setStatus(Status.CANCELED);
        Booking savedBooking = bookingRepo.save(bookingFromDB);
        return bookingMapper.toDto(savedBooking);
    }

    @Override
    @Transactional
    public List<BookingDto> expiredBookings() {
        List<Booking> bookingList = bookingRepo.findAllByStatusNotInAndCheckOutDateIs(
                Status.CANCELED, Status.PAID, LocalDate.now().plusDays(1L));
        if (!bookingList.isEmpty()) {
            bookingList.forEach(booking -> booking.setStatus(Status.EXPIRED));
            return bookingRepo.saveAll(bookingList).stream()
                    .map(bookingMapper::toDto).toList();
        }
        return List.of();
    }

    private Accommodation getAccommodationById(Long id) {
        return accommodationRepo.findById(id).orElseThrow(() ->
                new EntityNotFoundCustomException("Can't get accommodation by id = " + id));
    }

}
