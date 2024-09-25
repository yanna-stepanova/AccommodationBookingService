package stepanova.yana.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import stepanova.yana.dto.accommodation.AccommodationDto;
import stepanova.yana.dto.accommodation.AccommodationDtoWithoutLocationAndAmenities;
import stepanova.yana.dto.accommodation.CreateAccommodationRequestDto;
import stepanova.yana.dto.accommodation.UpdateAccommodationRequestDto;
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

        Location locationFromDB = locationRepo.findByCountryContainsIgnoreCaseAndCityContainsIgnoreCaseAndRegionContainsIgnoreCaseAndAddressContainsIgnoreCase(
                accommodation.getLocation().getCountry(),
                accommodation.getLocation().getCity(),
                accommodation.getLocation().getRegion(),
                accommodation.getLocation().getAddress())
                .orElseGet(() -> locationRepo.save(accommodation.getLocation()));
        accommodation.setLocation(locationFromDB);

        Set<Amenity> amenitySet = new HashSet<>();
        for (Amenity amenity: accommodation.getAmenities()) {
            Amenity amenityFromDB = amenityRepo.findByTitleContainsIgnoreCase(amenity.getTitle())
                    .orElseGet(() -> amenityRepo.save(amenity));
            amenitySet.add(amenityFromDB);
        }
        accommodation.setAmenities(amenitySet);

        return accommodationMapper.toDto(accommodationRepo.save(accommodation));
    }

    @Override
    @Transactional
    public AccommodationDto getAccommodationById(Long id) {
        return accommodationRepo.findById(id)
                .map(accommodationMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Accommodation with id: %s not found", id)));
    }

    @Override
    @Transactional
    public List<AccommodationDtoWithoutLocationAndAmenities> getAll() {
        return accommodationRepo.findAll().stream()
                .map(accommodationMapper::toDtoWithoutLocationAndAmenities)
                .toList();
    }

    @Override
    @Transactional
    public AccommodationDto updateAccommodationById(Long id, UpdateAccommodationRequestDto requestDto) {
        Accommodation oldAccommodation = accommodationRepo.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Can't get accommodation by id = " + id));
        Accommodation updatedAccommodation = accommodationMapper.updateAccommodationFromDto(oldAccommodation, requestDto);
        return accommodationMapper.toDto(updatedAccommodation);
    }

    @Override
    public void deleteById(Long id) {
        accommodationRepo.deleteById(id);
    }
}
