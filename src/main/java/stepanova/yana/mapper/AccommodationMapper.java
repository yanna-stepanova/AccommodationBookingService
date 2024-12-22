package stepanova.yana.mapper;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Collectors;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import stepanova.yana.config.MapperConfig;
import stepanova.yana.dto.accommodation.AccommodationDto;
import stepanova.yana.dto.accommodation.AccommodationDtoWithoutLocationAndAmenities;
import stepanova.yana.dto.accommodation.CreateAccommodationRequestDto;
import stepanova.yana.dto.accommodation.UpdateAccommodationRequestDto;
import stepanova.yana.dto.accommodation.UpdateAllAccommodationRequestDto;
import stepanova.yana.model.Accommodation;
import stepanova.yana.model.Amenity;
import stepanova.yana.model.Type;

@Mapper(config = MapperConfig.class, uses = {AmenityMapper.class, LocationMapper.class})
public interface AccommodationMapper {
    @Mapping(target = "amenities", ignore = true)
    @Mapping(target = "type", ignore = true)
    Accommodation toModel(CreateAccommodationRequestDto requestDto);

    AccommodationDto toDto(Accommodation accommodation);

    AccommodationDtoWithoutLocationAndAmenities toDtoWithoutLocationAndAmenities(
            Accommodation accommodation);

    @Mapping(target = "type", ignore = true)
    Accommodation updateAccommodationFromDto(@MappingTarget Accommodation accommodation,
                                             UpdateAccommodationRequestDto requestDto);

    @Mapping(target = "amenities", ignore = true)
    @Mapping(target = "location", ignore = true)
    @Mapping(target = "type", ignore = true)
    Accommodation updateAccommodationFromDto(@MappingTarget Accommodation accommodation,
                                             UpdateAllAccommodationRequestDto requestDto);

    @AfterMapping
    default void setType(@MappingTarget Accommodation accommodation,
                         CreateAccommodationRequestDto requestDto) {
        if (requestDto.typeName() != null) {
            accommodation.setType(Type.valueOf(requestDto.typeName().toUpperCase()));
        }
    }

    @AfterMapping
    default void setUpdatedType(@MappingTarget Accommodation accommodation,
                                UpdateAllAccommodationRequestDto requestDto) {
        if (requestDto.typeName() != null) {
            accommodation.setType(Type.valueOf(requestDto.typeName().toUpperCase()));
        }
    }

    @AfterMapping
    default void setUpdatedType(@MappingTarget Accommodation accommodation,
                                UpdateAccommodationRequestDto requestDto) {
        if (requestDto.typeName() != null) {
            accommodation.setType(Type.valueOf(requestDto.typeName().toUpperCase()));
        }
    }

    @AfterMapping
    default void setAmenitySet(@MappingTarget Accommodation accommodation,
                               CreateAccommodationRequestDto requestDto) {
        accommodation.setAmenities(Optional.ofNullable(requestDto.amenities())
                .map(amenities -> amenities.stream()
                        .map(Amenity::new)
                        .collect(Collectors.toSet()))
                .orElse(Collections.emptySet()));
    }

    @Named("accommodationFromId")
    default Accommodation accommodationFromId(Long accommodationId) {
        return Optional.ofNullable(accommodationId)
                .map(Accommodation::new)
                .orElse(null);
    }
}
