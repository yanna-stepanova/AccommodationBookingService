package stepanova.yana.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import stepanova.yana.dto.booking.BookingDto;
import stepanova.yana.dto.booking.BookingDtoWithoutDetails;
import stepanova.yana.dto.booking.CreateBookingRequestDto;
import stepanova.yana.mapper.BookingMapper;
import stepanova.yana.model.Accommodation;
import stepanova.yana.model.Booking;
import stepanova.yana.model.Status;
import stepanova.yana.model.User;
import stepanova.yana.repository.accommodation.AccommodationRepository;
import stepanova.yana.repository.booking.BookingRepository;
import stepanova.yana.service.BookingService;

@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepo;
    private final BookingMapper bookingMapper;
    private final AccommodationRepository accommodationRepo;

    @Override
    @Transactional
    public BookingDto save(User user, CreateBookingRequestDto requestDto) {
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
            return bookingMapper.toDto(null);
        }
        return bookingMapper.toDto(bookingRepo.save(booking));
    }

    @Override
    @Transactional
    public List<BookingDtoWithoutDetails> getAllByUserAndStatus(Long userId, String statusName) {
        return bookingRepo.findAllByUserIdAndStatus(userId, Status.getByType(statusName)).stream()
                .map(bookingMapper::toDtoWithoutDetails)
                .toList();
    }

    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        return null;
    }

    @Override
    public List<BookingDtoWithoutDetails> getAllByUser(Long userId) {
        return bookingRepo.findAllByUserId(userId).stream()
                .map(bookingMapper::toDtoWithoutDetails)
                .toList();
    }

    private Accommodation getAccommodationById(Long id) {
        return accommodationRepo.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Can't get accommodation by id = " + id));
    }
}
