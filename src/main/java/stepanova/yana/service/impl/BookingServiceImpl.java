package stepanova.yana.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import stepanova.yana.dto.booking.BookingDto;
import stepanova.yana.dto.booking.CreateBookingRequestDto;
import stepanova.yana.exception.BookingException;
import stepanova.yana.mapper.BookingMapper;
import stepanova.yana.model.Accommodation;
import stepanova.yana.model.Booking;
import stepanova.yana.model.Status;
import stepanova.yana.model.User;
import stepanova.yana.repository.accommodation.AccommodationRepository;
import stepanova.yana.repository.booking.BookingRepository;
import stepanova.yana.service.BookingService;

import java.util.List;

@RequiredArgsConstructor
@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepo;
    private final BookingMapper bookingMapper;
    private final AccommodationRepository accommodationRepo;

    @Override
    @Transactional
    public BookingDto save(User user, CreateBookingRequestDto requestDto) throws BookingException {
        Booking booking = bookingMapper.toModel(requestDto);
        Accommodation accommodationFromDB = getAccommodationById(booking.getAccommodation().getId());
        booking.setAccommodation(accommodationFromDB);
        booking.setUser(user);
        booking.setStatus(Status.PENDING);
        List<Booking> othersBooking = bookingRepo
                .findAllByAccommodationIdAndFromDateAndToDate(
                        booking.getAccommodation().getId(),
                        booking.getCheckInDate(),
                        booking.getCheckOutDate());
        if (othersBooking.size() >= accommodationFromDB.getAvailability()) {
            throw new BookingException("There is no available accommodation for these dates");
        }
        return bookingMapper.toDto(bookingRepo.save(booking));
    }

    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        return null;
    }

    private Accommodation getAccommodationById(Long id) {
        return accommodationRepo.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Can't get accommodation by id = " + id));
    }
}
