package stepanova.yana.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import stepanova.yana.config.MapperConfig;
import stepanova.yana.dto.booking.BookingDto;
import stepanova.yana.dto.booking.BookingDtoWithoutDetails;
import stepanova.yana.dto.booking.CreateBookingRequestDto;
import stepanova.yana.model.Booking;

@Mapper(config = MapperConfig.class, uses = {AccommodationMapper.class, UserMapper.class})
public interface BookingMapper {
    @Mapping(target = "accommodation", source = "accommodationId",
            qualifiedByName = "accommodationFromId")
    Booking toModel(CreateBookingRequestDto requestDto);

    BookingDto toDto(Booking booking);

    @Mapping(source = "accommodation.id", target = "accommodationId")
    @Mapping(source = "user.id", target = "userId")
    BookingDtoWithoutDetails toDtoWithoutDetails(Booking booking);
}
