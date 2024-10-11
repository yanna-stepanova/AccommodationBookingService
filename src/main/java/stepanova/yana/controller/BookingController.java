package stepanova.yana.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import stepanova.yana.dto.booking.BookingDto;
import stepanova.yana.dto.booking.BookingDtoWithoutDetails;
import stepanova.yana.dto.booking.CreateBookingRequestDto;
import stepanova.yana.dto.booking.UpdateBookingStatusRequestDto;
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
                                    @RequestBody @Valid CreateBookingRequestDto requestDto) {
        return bookingService.save(user, requestDto);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping(params = {"userId", "statusName"})
    @Operation(summary = "Get all bookings for some user and by some status",
            description = "Retrieves bookings based on user ID and their status")
    public List<BookingDtoWithoutDetails> getBookingsByUserAndStatus(
            @Positive @RequestParam Long userId,
            @RequestParam String statusName) {
        return bookingService.getAllByUserAndStatus(userId, statusName);
    }

    @GetMapping("/my")
    @Operation(summary = "Get all user's bookings",
            description = "Retrieves user's bookings")
    public List<BookingDtoWithoutDetails> getBookingsForOwner(@AuthenticationPrincipal User user) {
        return bookingService.getAllByUser(user.getId());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a booking by id for user",
            description = "Provides information about a specific booking for owner")
    public BookingDto getBookingForOwner(@AuthenticationPrincipal User user,
                                         @PathVariable @Positive Long id) {
        return bookingService.getBookingByIdAndUserId(user.getId(), id);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{id}/extra")
    @Operation(summary = "Get a booking by id for admin",
            description = "Provides information about a specific booking for admin(full access)")
    public BookingDto getBooking(@PathVariable @Positive Long id) {
        return bookingService.getBookingById(id);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update a booking by id",
            description = "Updates status of booking by id (only for admin)")
    public BookingDto updateBooking(@PathVariable @Positive Long id,
                                    @RequestBody UpdateBookingStatusRequestDto requestDto) {
        return bookingService.updateBookingById(id, requestDto);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel a booking by id for owner",
            description = "Enables the cancellation of booking")
    public BookingDto deleteBooking(@AuthenticationPrincipal User user,
                                    @PathVariable @Positive Long id) {
        return bookingService.cancelBookingById(user.getId(), id);
    }

}
