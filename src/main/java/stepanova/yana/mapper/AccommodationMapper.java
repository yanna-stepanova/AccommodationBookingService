package stepanova.yana.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import stepanova.yana.config.MapperConfig;
import stepanova.yana.dto.accommodation.AccommodationDto;
import stepanova.yana.dto.accommodation.CreateAccommodationRequestDto;
import stepanova.yana.model.Accommodation;

@Mapper(config = MapperConfig.class)
public interface AccommodationMapper {
    @Mapping(target = "amenities", ignore = true)
    Accommodation toModel(CreateAccommodationRequestDto requestDto);

    AccommodationDto toDto(Accommodation accommodation);
}
