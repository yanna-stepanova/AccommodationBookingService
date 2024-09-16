package stepanova.yana.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import stepanova.yana.dto.accommodation.AccommodationDto;
import stepanova.yana.dto.accommodation.CreateAccommodationRequestDto;
import stepanova.yana.mapper.AccommodationMapper;
import stepanova.yana.model.Accommodation;
import stepanova.yana.model.Amenity;
import stepanova.yana.model.Location;
import stepanova.yana.repository.accommodation.AccommodationRepository;
import stepanova.yana.repository.accommodation.AmenityRepository;
import stepanova.yana.repository.accommodation.LocationRepository;
import stepanova.yana.service.AccommodationService;

@RequiredArgsConstructor
@Service
public class AccommodationServiceImpl implements AccommodationService {
    private final AccommodationRepository accommodationRepo;
    private final AccommodationMapper accommodationMapper;
    private final LocationRepository locationRepo;
    private final AmenityRepository amenityRepo;

    @Override
    @Transactional
    public AccommodationDto save(CreateAccommodationRequestDto requestDto) {
        Accommodation accommodation = accommodationMapper.toModel(requestDto);

        Location savedLocation = locationRepo.save(accommodation.getLocation());
        accommodation.setLocation(savedLocation);

        Set<Amenity> amenitySet = new HashSet<>();
        for (Amenity amenity: accommodation.getAmenities()) {
            Amenity amenityFromDB = amenityRepo.findByTitleContainsIgnoreCase(amenity.getTitle())
                    .orElseGet(() -> amenityRepo.save(amenity));
            amenitySet.add(amenityFromDB);
        }
        accommodation.setAmenities(amenitySet);

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
