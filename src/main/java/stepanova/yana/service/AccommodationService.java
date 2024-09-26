package stepanova.yana.service;

import stepanova.yana.dto.accommodation.AccommodationDto;
import stepanova.yana.dto.accommodation.AccommodationDtoWithoutLocationAndAmenities;
import stepanova.yana.dto.accommodation.CreateAccommodationRequestDto;
import stepanova.yana.dto.accommodation.UpdateAccommodationAndAmenitiesRequestDto;
import stepanova.yana.dto.accommodation.UpdateAccommodationAndLocationRequestDto;
import stepanova.yana.dto.accommodation.UpdateAccommodationRequestDto;

import java.util.List;

public interface AccommodationService {
    AccommodationDto save(CreateAccommodationRequestDto requestDto);

    AccommodationDto getAccommodationById(Long id);

    List<AccommodationDtoWithoutLocationAndAmenities> getAll();

    AccommodationDto updateAccommodationById(Long id, UpdateAccommodationRequestDto requestDto);

    AccommodationDto updateAccommodationById(Long id, UpdateAccommodationAndLocationRequestDto requestDto);

    AccommodationDto updateAccommodationById(Long id, UpdateAccommodationAndAmenitiesRequestDto requestDto);

    void deleteById(Long id);
}
