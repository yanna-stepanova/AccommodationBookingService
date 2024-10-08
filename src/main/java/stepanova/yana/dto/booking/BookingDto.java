package stepanova.yana.dto.booking;

import java.io.Serializable;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stepanova.yana.dto.accommodation.AccommodationDtoWithoutAvailability;
import stepanova.yana.dto.user.UserResponseDto;
import stepanova.yana.model.Status;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookingDto implements Serializable {
    private Long id;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private AccommodationDtoWithoutAvailability accommodation;
    private UserResponseDto user;
    private Status status;

}
