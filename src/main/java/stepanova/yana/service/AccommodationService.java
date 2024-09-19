package stepanova.yana.service;

import stepanova.yana.dto.accommodation.AccommodationDto;
import stepanova.yana.dto.accommodation.CreateAccommodationRequestDto;

import java.util.List;

public interface AccommodationService {
    AccommodationDto save(CreateAccommodationRequestDto requestDto);

    AccommodationDto getAccommodationById(Long id);

    List<AccommodationDto> getAll();

    AccommodationDto updateAccommodation(Long id, CreateAccommodationRequestDto requestDto);

    void deleteById(Long id);
}
