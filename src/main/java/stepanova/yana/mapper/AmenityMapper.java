package stepanova.yana.mapper;

import org.mapstruct.Mapper;
import stepanova.yana.config.MapperConfig;
import stepanova.yana.dto.amenity.AmenityDto;
import stepanova.yana.dto.amenity.CreateAmenityRequestDto;
import stepanova.yana.model.Amenity;

@Mapper(config = MapperConfig.class)
public interface AmenityMapper {
    Amenity toModel(CreateAmenityRequestDto requestDto);

    AmenityDto toDto(Amenity amenity);
}
