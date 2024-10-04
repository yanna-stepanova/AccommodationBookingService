package stepanova.yana.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stepanova.yana.dto.booking.BookingDto;
import stepanova.yana.dto.booking.CreateBookingRequestDto;
import stepanova.yana.exception.BookingException;
import stepanova.yana.model.User;
import stepanova.yana.service.BookingService;

@Tag(name = "Booking manager", description = "Endpoints for managing bookings")
@RequiredArgsConstructor
@RestController
@RequestMapping("/bookings")
@Validated
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @Operation(summary = "Create a new booking of some accommodation",
            description = "Permits the creation of new accommodation booking")
    public BookingDto createBooking(@AuthenticationPrincipal User user,
                                    @RequestBody @Valid CreateBookingRequestDto requestDto) throws BookingException {
        return bookingService.save(user, requestDto);
    }

}
