package stepanova.yana.service;

import stepanova.yana.dto.booking.BookingDto;
import stepanova.yana.dto.booking.CreateBookingRequestDto;
import stepanova.yana.model.User;

public interface BookingService {
    BookingDto save(User user, CreateBookingRequestDto requestDto);

    BookingDto getBookingById(Long userId, Long bookingId);
}
