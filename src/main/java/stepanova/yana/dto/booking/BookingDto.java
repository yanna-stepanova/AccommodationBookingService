package stepanova.yana.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stepanova.yana.dto.accommodation.AccommodationDto;
import stepanova.yana.dto.user.UserResponseDto;
import stepanova.yana.model.Status;

import java.io.Serializable;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookingDto implements Serializable {
    private Long id;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private AccommodationDto accommodation;
    private UserResponseDto user;
    private Status status;

}
