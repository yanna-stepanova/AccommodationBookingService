package stepanova.yana.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import stepanova.yana.config.MapperConfig;
import stepanova.yana.dto.accommodation.AccommodationDto;
import stepanova.yana.dto.accommodation.CreateAccommodationRequestDto;
import stepanova.yana.model.Accommodation;
import stepanova.yana.model.Type;

@Mapper(config = MapperConfig.class)
public interface AccommodationMapper {
    @Mapping(target = "amenities", ignore = true)
    @Mapping(target = "type", ignore = true)
    Accommodation toModel(CreateAccommodationRequestDto requestDto);

    @AfterMapping
    default void setType(@MappingTarget Accommodation accommodation, CreateAccommodationRequestDto requestDto) {
        if (requestDto.typeName() != null) {
           accommodation.setType(Type.getByType(requestDto.typeName()));
        }
    }

    AccommodationDto toDto(Accommodation accommodation);
}
