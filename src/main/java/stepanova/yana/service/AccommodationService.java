package stepanova.yana.service;

import java.util.List;
import org.springframework.data.domain.Pageable;
import stepanova.yana.dto.accommodation.AccommodationDto;
import stepanova.yana.dto.accommodation.AccommodationDtoWithoutLocationAndAmenities;
import stepanova.yana.dto.accommodation.CreateAccommodationRequestDto;
import stepanova.yana.dto.accommodation.UpdateAccommodationRequestDto;
import stepanova.yana.dto.accommodation.UpdateAllAccommodationRequestDto;

public interface AccommodationService {
    AccommodationDto save(CreateAccommodationRequestDto requestDto);

    AccommodationDto getAccommodationById(Long id);

    List<AccommodationDtoWithoutLocationAndAmenities> getAll(Pageable pageable);

    AccommodationDto updateAccommodationById(Long id, UpdateAccommodationRequestDto requestDto);

    AccommodationDto updateAccommodationById(Long id, UpdateAllAccommodationRequestDto requestDto);

    void deleteById(Long id);
}
