package stepanova.yana.service;

import stepanova.yana.dto.booking.BookingDto;
import stepanova.yana.dto.booking.BookingDtoWithoutDetails;
import stepanova.yana.dto.booking.CreateBookingRequestDto;
import stepanova.yana.model.User;

import java.util.List;

public interface BookingService {
    BookingDto save(User user, CreateBookingRequestDto requestDto);

    List<BookingDtoWithoutDetails> getAllByUserAndStatus(Long userId, String statusName);

    BookingDto getBookingById(Long userId, Long bookingId);

    List<BookingDtoWithoutDetails> getAllByUser(Long userId);

}
