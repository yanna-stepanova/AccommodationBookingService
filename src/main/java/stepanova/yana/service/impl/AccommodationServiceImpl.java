package stepanova.yana.service.impl;

import java.util.List;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import stepanova.yana.dto.accommodation.AccommodationDto;
import stepanova.yana.dto.accommodation.CreateAccommodationRequestDto;
import stepanova.yana.mapper.AccommodationMapper;
import stepanova.yana.model.Accommodation;
import stepanova.yana.model.Location;
import stepanova.yana.repository.accommodation.AccommodationRepository;
import stepanova.yana.repository.accommodation.LocationRepository;
import stepanova.yana.service.AccommodationService;

@RequiredArgsConstructor
@Service
public class AccommodationServiceImpl implements AccommodationService {
    private final AccommodationRepository accommodationRepo;
    private final AccommodationMapper accommodationMapper;
    private final LocationRepository locationRepo;

    @Override
    @Transactional
    public AccommodationDto save(CreateAccommodationRequestDto requestDto) {
        Accommodation accommodation = accommodationMapper.toModel(requestDto);
        Location savedLocation = locationRepo.save(accommodation.getLocation());
        accommodation.setLocation(savedLocation);
        Accommodation savedAcc = accommodationRepo.save(accommodation);
        return accommodationMapper.toDto(savedAcc);
    }

    @Override
    public AccommodationDto getAccommodationById() {
        return null;
    }

    @Override
    @Transactional
    public List<AccommodationDto> getAll() {
        return accommodationRepo.findAll().stream()
                .map(accommodationMapper::toDto)
                .toList();
    }

    @Override
    public AccommodationDto updateAccommodation(Long id, CreateAccommodationRequestDto requestDto) {
        return null;
    }

    @Override
    public void deleteById(Long id) {

    }
}
