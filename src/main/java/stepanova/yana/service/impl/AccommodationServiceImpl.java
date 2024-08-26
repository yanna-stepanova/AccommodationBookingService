package stepanova.yana.service.impl;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import stepanova.yana.dto.accommodation.AccommodationDto;
import stepanova.yana.dto.accommodation.CreateAccommodationRequestDto;
import stepanova.yana.mapper.AccommodationMapper;
import stepanova.yana.model.Accommodation;
import stepanova.yana.repository.accommodation.AccommodationRepository;
import stepanova.yana.service.AccommodationService;

@RequiredArgsConstructor
@Service
public class AccommodationServiceImpl implements AccommodationService {
    private final AccommodationRepository accommodationRepo;
    private final AccommodationMapper accommodationMapper;

    @Override
    public AccommodationDto save(CreateAccommodationRequestDto requestDto) {
        Accommodation accommodation = accommodationMapper.toModel(requestDto);
        Accommodation savedAcc = accommodationRepo.save(accommodation);
        AccommodationDto accommodationDto = accommodationMapper.toDto(savedAcc);
        return accommodationDto;
    }

    @Override
    public AccommodationDto getAccommodationById() {
        return null;
    }

    @Override
    public List<AccommodationDto> getAll() {
        List<AccommodationDto> accommodationDtos = accommodationRepo.findAll().stream()
                .map(accommodationMapper::toDto)
                .toList();
        return accommodationDtos;
    }

    @Override
    public AccommodationDto updateAccommodation(Long id, CreateAccommodationRequestDto requestDto) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }
}
