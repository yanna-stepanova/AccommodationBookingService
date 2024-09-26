package stepanova.yana.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import stepanova.yana.dto.accommodation.AccommodationDto;
import stepanova.yana.dto.accommodation.AccommodationDtoWithoutLocationAndAmenities;
import stepanova.yana.dto.accommodation.CreateAccommodationRequestDto;
import stepanova.yana.dto.accommodation.UpdateAccommodationAndAmenitiesRequestDto;
import stepanova.yana.dto.accommodation.UpdateAccommodationAndLocationRequestDto;
import stepanova.yana.dto.accommodation.UpdateAccommodationRequestDto;
import stepanova.yana.dto.amenity.CreateAmenityRequestDto;
import stepanova.yana.mapper.AccommodationMapper;
import stepanova.yana.mapper.AmenityMapper;
import stepanova.yana.mapper.LocationMapper;
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
    private final LocationMapper locationMapper;
    private final AmenityRepository amenityRepo;
    private final AmenityMapper amenityMapper;

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
            Amenity amenityFromDB = getSavedAmenityByTitle(amenity);
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
        Accommodation oldAccommodation = getAccommodationByIdFromDB(id);
        Accommodation updatedAccommodation = accommodationMapper.updateAccommodationFromDto(oldAccommodation, requestDto);
        return accommodationMapper.toDto(accommodationRepo.save(updatedAccommodation));
    }

    @Override
    @Transactional
    public AccommodationDto updateAccommodationById(Long id, UpdateAccommodationAndLocationRequestDto requestDto) {
        Accommodation oldAccommodation = getAccommodationByIdFromDB(id);
        Location currentLocation = oldAccommodation.getLocation();
        if (currentLocation != null) {
            currentLocation = locationMapper.updateLocationFromDto(currentLocation, requestDto.location());
        } else {
            currentLocation = locationMapper.toModel(requestDto.location());
        }
        currentLocation = locationRepo.save(currentLocation);

        Accommodation updatedAccommodation = accommodationMapper.updateAccommodationFromDto(oldAccommodation, requestDto);
        updatedAccommodation.setLocation(currentLocation);
        return accommodationMapper.toDto(accommodationRepo.save(updatedAccommodation));
    }

    @Override
    @Transactional
    public AccommodationDto updateAccommodationById(Long id, UpdateAccommodationAndAmenitiesRequestDto requestDto) {
        Set<Amenity> amenitiesFromDB = getAmenities(requestDto.amenities());
        Accommodation accommodationFromDB = getAccommodationByIdFromDB(id);
        Accommodation updatedAccommodation = accommodationMapper.updateAccommodationFromDto(accommodationFromDB, requestDto);
        updatedAccommodation.setAmenities(amenitiesFromDB);
        return accommodationMapper.toDto(accommodationRepo.save(updatedAccommodation));
    }

    @Override
    public void deleteById(Long id) {
        accommodationRepo.deleteById(id);
    }

    private Accommodation getAccommodationByIdFromDB(Long id) {
        return accommodationRepo.findById(id).orElseThrow(() ->
                new EntityNotFoundException("Can't get accommodation by id = " + id));
    }

    @Transactional
    private Set<Amenity> getAmenities(Set<CreateAmenityRequestDto> amenityRequestDtos) {
        Set<Amenity> amenitySet = new HashSet<>();
        for (CreateAmenityRequestDto amenityRequestDto: amenityRequestDtos) {
            Amenity amenity = amenityMapper.toModel(amenityRequestDto);
            Amenity amenityFromDB = getSavedAmenityByTitle(amenity);
            if (amenity.getDescription() != null) {
                amenityFromDB.setDescription(amenity.getDescription());
            }
            amenitySet.add(amenityRepo.save(amenityFromDB));
        }
        return amenitySet;
    }

    private Amenity getSavedAmenityByTitle(Amenity amenity) {
        return amenityRepo.findByTitleContainsIgnoreCase(amenity.getTitle())
                .orElseGet(() -> amenityRepo.save(amenity));
    }
}
