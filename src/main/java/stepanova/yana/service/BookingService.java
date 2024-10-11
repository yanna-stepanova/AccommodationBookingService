package stepanova.yana.service;

import java.util.List;
import stepanova.yana.dto.booking.BookingDto;
import stepanova.yana.dto.booking.BookingDtoWithoutDetails;
import stepanova.yana.dto.booking.CreateBookingRequestDto;
import stepanova.yana.dto.booking.UpdateBookingStatusRequestDto;
import stepanova.yana.model.User;

public interface BookingService {
    BookingDto save(User user, CreateBookingRequestDto requestDto);

    List<BookingDtoWithoutDetails> getAllByUserAndStatus(Long userId, String statusName);

    BookingDto getBookingByIdAndUserId(Long userId, Long bookingId);

    List<BookingDtoWithoutDetails> getAllByUser(Long userId);

    BookingDto getBookingById(Long bookingId);

    BookingDto updateBookingById(Long bookingId, UpdateBookingStatusRequestDto requestDto);

    BookingDto cancelBookingById(Long userId, Long bookingId);
}
