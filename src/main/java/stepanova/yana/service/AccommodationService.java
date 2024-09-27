package stepanova.yana.service;

import stepanova.yana.dto.accommodation.AccommodationDto;
import stepanova.yana.dto.accommodation.AccommodationDtoWithoutLocationAndAmenities;
import stepanova.yana.dto.accommodation.CreateAccommodationRequestDto;
import stepanova.yana.dto.accommodation.UpdateAccommodationRequestDto;
import stepanova.yana.dto.accommodation.UpdateAllAccommodationRequestDto;

import java.util.List;

public interface AccommodationService {
    AccommodationDto save(CreateAccommodationRequestDto requestDto);

    AccommodationDto getAccommodationById(Long id);

    List<AccommodationDtoWithoutLocationAndAmenities> getAll();

    AccommodationDto updateAccommodationById(Long id, UpdateAccommodationRequestDto requestDto);

    AccommodationDto updateAccommodationById(Long id, UpdateAllAccommodationRequestDto requestDto);

    void deleteById(Long id);
}
