package stepanova.yana.mapper;

import org.mapstruct.Mapper;
import stepanova.yana.config.MapperConfig;
import stepanova.yana.dto.location.CreateLocationRequestDto;
import stepanova.yana.model.Location;

@Mapper(config = MapperConfig.class)
public interface LocationMapper {
    Location toModel(CreateLocationRequestDto requestDto);
}
