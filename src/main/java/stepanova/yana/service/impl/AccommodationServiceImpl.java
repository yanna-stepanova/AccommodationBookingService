package stepanova.yana.service.impl;

import jakarta.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import stepanova.yana.dto.accommodation.AccommodationDto;
import stepanova.yana.dto.accommodation.AccommodationDtoWithoutLocationAndAmenities;
import stepanova.yana.dto.accommodation.CreateAccommodationRequestDto;
import stepanova.yana.dto.accommodation.UpdateAccommodationRequestDto;
import stepanova.yana.dto.accommodation.UpdateAllAccommodationRequestDto;
import stepanova.yana.dto.amenity.CreateAmenityRequestDto;
import stepanova.yana.dto.location.CreateLocationRequestDto;
import stepanova.yana.exception.CustomTelegramApiException;
import stepanova.yana.exception.EntityNotFoundCustomException;
import stepanova.yana.mapper.AccommodationMapper;
import stepanova.yana.mapper.AmenityMapper;
import stepanova.yana.model.Accommodation;
import stepanova.yana.model.Amenity;
import stepanova.yana.model.Location;
import stepanova.yana.repository.AccommodationRepository;
import stepanova.yana.repository.AmenityRepository;
import stepanova.yana.repository.LocationRepository;
import stepanova.yana.service.AccommodationService;
import stepanova.yana.telegram.TelegramNotificationService;
import stepanova.yana.util.MessageFormatter;

@RequiredArgsConstructor
@Service
public class AccommodationServiceImpl implements AccommodationService {
    private static final Logger log = LogManager.getLogger(AccommodationServiceImpl.class);
    private final AccommodationRepository accommodationRepo;
    private final AccommodationMapper accommodationMapper;
    private final LocationRepository locationRepo;
    private final AmenityRepository amenityRepo;
    private final AmenityMapper amenityMapper;
    private final TelegramNotificationService telegramNote;

    @Override
    @Transactional
    public AccommodationDto save(CreateAccommodationRequestDto requestDto) {
        Accommodation accommodation = accommodationMapper.toModel(requestDto);

        Location locationFromDB = locationRepo
                .findByCountryAndCityAndRegionAndAddressAllIgnoreCase(
                        accommodation.getLocation().getCountry(),
                        accommodation.getLocation().getCity(),
                        accommodation.getLocation().getRegion(),
                        accommodation.getLocation().getAddress())
                .orElseGet(() -> locationRepo.save(accommodation.getLocation()));
        accommodation.setLocation(locationFromDB);

        Set<Amenity> amenitySet = new HashSet<>();
        for (Amenity amenity : accommodation.getAmenities()) {
            Amenity amenityFromDB = getAmenityByTitleFromDB(amenity);
            amenitySet.add(amenityFromDB);
        }
        accommodation.setAmenities(amenitySet);
        Accommodation savedAccommodation = accommodationRepo.save(accommodation);
        notifyTelegramAsync(savedAccommodation, "New");
        return accommodationMapper.toDto(savedAccommodation);
    }

    @Override
    @Transactional
    public AccommodationDto getAccommodationById(Long id) {
        return accommodationRepo.findById(id)
                .map(accommodationMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundCustomException(
                        String.format("Accommodation with id: %s not found", id)));
    }

    @Override
    public List<AccommodationDtoWithoutLocationAndAmenities> getAll(Pageable pageable) {
        return accommodationRepo.findAll(pageable).stream()
                .map(accommodationMapper::toDtoWithoutLocationAndAmenities)
                .toList();
    }

    @Override
    @Transactional
    public AccommodationDto updateAccommodationById(Long id,
                                                    UpdateAccommodationRequestDto requestDto) {
        Accommodation updatedAccommodation = accommodationRepo.save(
                accommodationMapper.updateAccommodationFromDto(
                getAccommodationByIdFromDB(id), requestDto));
        notifyTelegramAsync(updatedAccommodation, "Update");
        return accommodationMapper.toDto(updatedAccommodation);
    }

    @Override
    @Transactional
    public AccommodationDto updateAccommodationById(Long id,
                                                    UpdateAllAccommodationRequestDto requestDto) {
        Accommodation accommodationFromDB = getAccommodationByIdFromDB(id);
        Location updatedLocation = getSavedLocation(requestDto.location());
        Set<Amenity> updatedAmenities = getSavedAmenities(requestDto.amenities());

        accommodationFromDB.setLocation(updatedLocation);
        accommodationFromDB.setAmenities(updatedAmenities);

        Accommodation updatedAccommodation = accommodationMapper.updateAccommodationFromDto(
                accommodationFromDB, requestDto);
        notifyTelegramAsync(updatedAccommodation, "Deep update");
        return accommodationMapper.toDto(accommodationRepo.save(updatedAccommodation));
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        accommodationRepo.deleteById(id);
    }

    private Location getSavedLocation(CreateLocationRequestDto locationRequestDto) {
        return locationRepo.findByCountryAndCityAndRegionAndAddressAllIgnoreCase(
                locationRequestDto.country(),
                locationRequestDto.city(),
                locationRequestDto.region(),
                locationRequestDto.address()).orElse(new Location());
    }

    private Accommodation getAccommodationByIdFromDB(Long id) {
        return accommodationRepo.findById(id).orElseThrow(() ->
                new EntityNotFoundCustomException("Can't get accommodation by id = " + id));
    }

    private Set<Amenity> getSavedAmenities(Set<CreateAmenityRequestDto> amenityRequestDtos) {
        Set<Amenity> amenitySet = new HashSet<>();
        for (CreateAmenityRequestDto amenityRequestDto : amenityRequestDtos) {
            Amenity amenity = amenityMapper.toModel(amenityRequestDto);
            Amenity amenityFromDB = getAmenityByTitleFromDB(amenity);
            if (amenity.getDescription() != null) {
                amenityFromDB.setDescription(amenity.getDescription());
            }
            amenitySet.add(amenityRepo.save(amenityFromDB));
        }
        return amenitySet;
    }

    private Amenity getAmenityByTitleFromDB(Amenity amenity) {
        return amenityRepo.findByTitleContainsIgnoreCase(amenity.getTitle())
                .orElseGet(() -> amenityRepo.save(amenity));
    }

    private void publishEvent(Accommodation accommodation, String option) {
        String message = MessageFormatter.formatAccommodationMessage(accommodation, option);
        try {
            telegramNote.sendMessage(message);
        } catch (CustomTelegramApiException ex) {
            log.warn("Failed to notify Telegram for accommodation ID {}: {}",
                    accommodation.getId(), ex.getMessage());
        }
    }

    private void notifyTelegramAsync(Accommodation accommodation, String action) {
        CompletableFuture.runAsync(() -> publishEvent(accommodation, action));
    }
}
