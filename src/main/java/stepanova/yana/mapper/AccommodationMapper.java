package stepanova.yana.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import stepanova.yana.config.MapperConfig;
import stepanova.yana.dto.accommodation.AccommodationDto;
import stepanova.yana.dto.accommodation.AccommodationDtoWithoutLocationAndAmenities;
import stepanova.yana.dto.accommodation.UpdateAccommodationRequestDto;
import stepanova.yana.dto.accommodation.CreateAccommodationRequestDto;
import stepanova.yana.model.Accommodation;
import stepanova.yana.model.Amenity;
import stepanova.yana.model.Type;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(config = MapperConfig.class, uses = AmenityMapper.class)
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

    @AfterMapping
    default void setAmenitySet(@MappingTarget Accommodation accommodation, CreateAccommodationRequestDto requestDto) {
        if (requestDto.amenities() == null) {
            accommodation.setAmenities(Set.of());
            return;
        }
        accommodation.setAmenities(requestDto.amenities().stream()
                .map(Amenity::new)
                .collect(Collectors.toSet())
        );
    }

    AccommodationDto toDto(Accommodation accommodation);

    AccommodationDtoWithoutLocationAndAmenities toDtoWithoutLocationAndAmenities(Accommodation accommodation);

    /*@Mapping(target = "amenities", ignore = true)*/
    @Mapping(target = "type", ignore = true)
    Accommodation updateAccommodationFromDto(@MappingTarget Accommodation accommodation,
                                             UpdateAccommodationRequestDto requestDto);

    @AfterMapping
    default void setTypeUpdate(@MappingTarget Accommodation accommodation, UpdateAccommodationRequestDto requestDto) {
        if (requestDto.typeName() != null) {
            accommodation.setType(Type.getByType(requestDto.typeName()));
        }
    }
}
