package stepanova.yana.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import stepanova.yana.config.MapperConfig;
import stepanova.yana.dto.booking.BookingDto;
import stepanova.yana.dto.booking.BookingDtoWithoutDetails;
import stepanova.yana.dto.booking.CreateBookingRequestDto;
import stepanova.yana.dto.booking.UpdateBookingStatusRequestDto;
import stepanova.yana.model.Booking;
import stepanova.yana.model.Status;

@Mapper(config = MapperConfig.class, uses = {AccommodationMapper.class, UserMapper.class})
public interface BookingMapper {
    @Mapping(target = "accommodation", source = "accommodationId",
            qualifiedByName = "accommodationFromId")
    Booking toModel(CreateBookingRequestDto requestDto);

    BookingDto toDto(Booking booking);

    @Mapping(source = "accommodation.id", target = "accommodationId")
    @Mapping(source = "user.id", target = "userId")
    BookingDtoWithoutDetails toDtoWithoutDetails(Booking booking);

    @Mapping(target = "status", ignore = true)
    Booking updateBookingFromDto(@MappingTarget Booking booking,
                                 UpdateBookingStatusRequestDto requestDto);

    @AfterMapping
    default void setUpdatedStatus(@MappingTarget Booking booking,
                                  UpdateBookingStatusRequestDto requestDto) {
        if (requestDto.statusName() != null) {
            booking.setStatus(Status.valueOf(requestDto.statusName().toUpperCase()));
        }
    }
}
