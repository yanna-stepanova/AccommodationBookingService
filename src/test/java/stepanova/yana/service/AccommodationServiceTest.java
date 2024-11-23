package stepanova.yana.service;

import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testcontainers.shaded.org.apache.commons.lang3.builder.EqualsBuilder;
import stepanova.yana.dto.accommodation.AccommodationDto;
import stepanova.yana.dto.accommodation.AccommodationDtoWithoutLocationAndAmenities;
import stepanova.yana.dto.accommodation.CreateAccommodationRequestDto;
import stepanova.yana.dto.accommodation.UpdateAccommodationRequestDto;
import stepanova.yana.dto.accommodation.UpdateAllAccommodationRequestDto;
import stepanova.yana.dto.amenity.AmenityDto;
import stepanova.yana.dto.amenity.CreateAmenityRequestDto;
import stepanova.yana.dto.location.CreateLocationRequestDto;
import stepanova.yana.dto.location.LocationDto;
import stepanova.yana.mapper.AccommodationMapper;
import stepanova.yana.mapper.AmenityMapper;
import stepanova.yana.model.Accommodation;
import stepanova.yana.model.Amenity;
import stepanova.yana.model.Location;
import stepanova.yana.model.Type;
import stepanova.yana.repository.accommodation.AccommodationRepository;
import stepanova.yana.repository.accommodation.AmenityRepository;
import stepanova.yana.repository.accommodation.LocationRepository;
import stepanova.yana.service.impl.AccommodationServiceImpl;
import stepanova.yana.telegram.TelegramNotificationService;

@ExtendWith(MockitoExtension.class)
class AccommodationServiceTest {
    @Mock
    private AccommodationRepository accommodationRepo;
    @Mock
    private LocationRepository locationRepo;
    @Mock
    private AmenityRepository amenityRepo;
    @Mock
    private TelegramNotificationService telegramService;
    @Mock
    private AmenityMapper amenityMapper;
    @Mock
    private AccommodationMapper accommodationMapper;
    @InjectMocks
    private AccommodationServiceImpl accommodationService;

    @Test
    @DisplayName("Get correct AccommodationDto for valid requestDto")
    void save_WithValidRequestDto_ReturnAccommodationDto() {
        //Given
        CreateAccommodationRequestDto requestDto = new CreateAccommodationRequestDto(
                "Hostel",
                new CreateLocationRequestDto("Ukraine", "Rivne", "Rivne region",
                        null,"Kyivska street, 10", null),
                "cupsule room (6 places)",
                Set.of(), BigDecimal.valueOf(7), 6);

        Location location = new Location();
        location.setId(7L);
        location.setCountry(requestDto.location().country());
        location.setCity(requestDto.location().city());
        location.setRegion(requestDto.location().region());
        location.setZipCode(requestDto.location().zipCode());
        location.setAddress(requestDto.location().address());
        location.setDescription(requestDto.location().description());

        Accommodation accommodation = new Accommodation();
        accommodation.setType(Type.getByType(requestDto.typeName()));
        accommodation.setLocation(location);
        accommodation.setAmenities(Set.of());
        accommodation.setSize(requestDto.size());
        accommodation.setDailyRate(requestDto.dailyRate());
        accommodation.setAvailability(requestDto.availability());

        AccommodationDto expected = new AccommodationDto(5L,
                accommodation.getType(),
                new LocationDto(7L, location.getCountry(), location.getCity(), location.getRegion(),
                        location.getZipCode(), location.getAddress(), location.getDescription()),
                accommodation.getSize(), Set.of(),
                accommodation.getDailyRate(),
                accommodation.getAvailability());

        Mockito.when(accommodationMapper.toModel(requestDto)).thenReturn(accommodation);
        Mockito.when(locationRepo.findByCountryAndCityAndRegionAndAddressAllIgnoreCase(
                requestDto.location().country(),
                requestDto.location().city(),
                requestDto.location().region(),
                requestDto.location().address())).thenReturn(Optional.of(location));
        Mockito.lenient().when(locationRepo.save(location)).thenReturn(location);
        Mockito.when(accommodationRepo.save(accommodation)).thenReturn(accommodation);
        Mockito.when(accommodationMapper.toDto(accommodation)).thenReturn(expected);

        //When
        AccommodationDto actual = accommodationService.save(requestDto);

        //Then
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @Test
    @DisplayName("Get correct AccommodationDto for existing accommodation id")
    void getAccommodationById_WithExistingAccommodationId_ReturnAccommodationDto() {
        //Given
        Long accommodationId = 4L;
        Accommodation accommodation = new Accommodation();
        accommodation.setId(accommodationId);
        accommodation.setType(Type.HOSTEL);
        accommodation.setLocation(new Location());
        accommodation.setAmenities(Set.of());
        accommodation.setSize("cupsule room (6 places)");
        accommodation.setDailyRate(BigDecimal.valueOf(7));
        accommodation.setAvailability(6);
        Mockito.when(accommodationRepo.findById(accommodationId))
                .thenReturn(Optional.of(accommodation));

        AccommodationDto expected = new AccommodationDto(
                accommodation.getId(),
                accommodation.getType(),
                new LocationDto(),
                accommodation.getSize(), Set.of(),
                accommodation.getDailyRate(),
                accommodation.getAvailability());
        Mockito.when(accommodationMapper.toDto(accommodation)).thenReturn(expected);

        //When
        AccommodationDto actual = accommodationService.getAccommodationById(accommodationId);

        //Then
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @Test
    @DisplayName("Exception: if get AccommodationDto for non-existing accommodation id")
    void getAccommodationById_WithNonExistingAccommodationId_ReturnException() {
        //Given
        Long accommodationId = 4L;

        Mockito.when(accommodationRepo.findById(accommodationId)).thenReturn(Optional.empty());

        //When
        Exception exception = Assertions.assertThrows(
                EntityNotFoundException.class,
                () -> accommodationService.getAccommodationById(accommodationId));

        //Then
        String expected = String.format("Accommodation with id: %s not found", accommodationId);
        String actual = exception.getMessage();
        Assertions.assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Get a list of all AccommodationDtoWithoutLocationAndAmenities")
    void getAll_ReturnTwo() {
        //Given
        Accommodation accommodationOne = new Accommodation();
        accommodationOne.setId(1L);
        accommodationOne.setType(Type.HOSTEL);
        accommodationOne.setLocation(new Location());
        accommodationOne.setAmenities(Set.of());
        accommodationOne.setSize("cupsule room (6 places)");
        accommodationOne.setDailyRate(BigDecimal.valueOf(7));
        accommodationOne.setAvailability(6);

        Accommodation accommodationTwo = new Accommodation();
        accommodationTwo.setId(2L);
        accommodationTwo.setType(Type.HOTEL);
        accommodationTwo.setLocation(new Location());
        accommodationTwo.setAmenities(Set.of());
        accommodationTwo.setSize("1 bedroom");
        accommodationTwo.setDailyRate(BigDecimal.valueOf(35));
        accommodationTwo.setAvailability(10);

        AccommodationDtoWithoutLocationAndAmenities dtoOne = new
                AccommodationDtoWithoutLocationAndAmenities(accommodationOne.getId(),
                accommodationOne.getType(),
                accommodationOne.getSize(),
                accommodationOne.getDailyRate(),
                accommodationOne.getAvailability());

        AccommodationDtoWithoutLocationAndAmenities dtoTwo = new
                AccommodationDtoWithoutLocationAndAmenities(accommodationTwo.getId(),
                accommodationTwo.getType(),
                accommodationTwo.getSize(),
                accommodationTwo.getDailyRate(),
                accommodationTwo.getAvailability());
        List<Accommodation> accommodationList = List.of(accommodationOne, accommodationTwo);

        Mockito.when(accommodationMapper.toDtoWithoutLocationAndAmenities(accommodationOne))
                .thenReturn(dtoOne);
        Mockito.when(accommodationMapper.toDtoWithoutLocationAndAmenities(accommodationTwo))
                .thenReturn(dtoTwo);
        Mockito.when(accommodationRepo.findAll()).thenReturn(accommodationList);

        //When
        List<AccommodationDtoWithoutLocationAndAmenities> expected = List.of(dtoOne, dtoTwo);
        List<AccommodationDtoWithoutLocationAndAmenities> actual = accommodationService.getAll();

        //Then
        Assertions.assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            Assertions.assertTrue(
                    EqualsBuilder.reflectionEquals(expected.get(i), actual.get(i)));
        }
    }

    @Test
    @DisplayName("Get updated AccommodationDto by existing id and valid requestDto")
    void updateAccommodationById_WithExistingIdAndValidRequestDto_ReturnAccommodationDto() {
        //Given
        Long accommodationId = 3L;
        Accommodation oldAccommodation = new Accommodation();
        oldAccommodation.setId(accommodationId);
        oldAccommodation.setType(Type.HOSTEL);
        oldAccommodation.setLocation(new Location());
        oldAccommodation.setAmenities(Set.of());
        oldAccommodation.setSize("cupsule room (6 places)");
        oldAccommodation.setDailyRate(BigDecimal.valueOf(7));
        oldAccommodation.setAvailability(6);

        UpdateAccommodationRequestDto requestDto = new UpdateAccommodationRequestDto(
                "hotel", "1 double room", BigDecimal.valueOf(16), 3);

        Accommodation updatedAccommodation = new Accommodation();
        updatedAccommodation.setId(accommodationId);
        updatedAccommodation.setType(Type.getByType(requestDto.typeName()));
        updatedAccommodation.setLocation(oldAccommodation.getLocation());
        updatedAccommodation.setAmenities(oldAccommodation.getAmenities());
        updatedAccommodation.setSize(requestDto.size());
        updatedAccommodation.setDailyRate(requestDto.dailyRate());
        updatedAccommodation.setAvailability(requestDto.availability());

        AccommodationDto expected = new AccommodationDto(accommodationId,
                updatedAccommodation.getType(),
                new LocationDto(),
                updatedAccommodation.getSize(),
                Set.of(new AmenityDto()),
                updatedAccommodation.getDailyRate(),
                updatedAccommodation.getAvailability());

        Mockito.when(accommodationRepo.findById(accommodationId))
                .thenReturn(Optional.of(oldAccommodation));
        Mockito.when(accommodationMapper.updateAccommodationFromDto(oldAccommodation, requestDto))
                .thenReturn(updatedAccommodation);
        Mockito.when(accommodationRepo.save(updatedAccommodation)).thenReturn(updatedAccommodation);
        Mockito.when(accommodationMapper.toDto(updatedAccommodation)).thenReturn(expected);

        //When
        AccommodationDto actual = accommodationService.updateAccommodationById(
                accommodationId, requestDto);

        //Then
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }

    @Test
    @DisplayName("Get deep updated AccommodationDto by existing id and valid requestDto")
    void updateAccommodationById_WithExistingIdAndValidFullRequestDtoWith_ReturnAccommodationDto() {
        //Given
        Long accommodationId = 1L;
        Accommodation oldAccommodation = new Accommodation();
        oldAccommodation.setId(accommodationId);
        oldAccommodation.setType(Type.HOSTEL);
        oldAccommodation.setLocation(new Location());
        oldAccommodation.setAmenities(Set.of());
        oldAccommodation.setSize("cupsule room (6 places)");
        oldAccommodation.setDailyRate(BigDecimal.valueOf(7));
        oldAccommodation.setAvailability(6);

        CreateAmenityRequestDto amenityRequestDto = new CreateAmenityRequestDto("TV", "Televition");
        UpdateAllAccommodationRequestDto requestDto = new UpdateAllAccommodationRequestDto(
                "house",
                new CreateLocationRequestDto(
                        "Country",
                        "City",
                        "Region",
                        "zipCode",
                        "Address",
                        "Description"),
                "some room size",
                Set.of(amenityRequestDto),
                BigDecimal.valueOf(20), 1);

        Location location = new Location();
        location.setId(1L);
        location.setCountry(requestDto.location().country());
        location.setCity(requestDto.location().city());
        location.setRegion(requestDto.location().region());
        location.setZipCode(requestDto.location().zipCode());
        location.setAddress(requestDto.location().address());
        location.setDescription(requestDto.location().description());

        Amenity amenity = new Amenity();
        amenity.setTitle("TV");
        amenity.setDescription("Televition");

        Accommodation updatedAccommodation = new Accommodation();
        updatedAccommodation.setId(oldAccommodation.getId());
        updatedAccommodation.setId(accommodationId);
        updatedAccommodation.setType(Type.getByType(requestDto.typeName()));
        updatedAccommodation.setLocation(location);
        updatedAccommodation.setAmenities(Set.of(amenity));
        updatedAccommodation.setSize(requestDto.size());
        updatedAccommodation.setDailyRate(requestDto.dailyRate());
        updatedAccommodation.setAvailability(requestDto.availability());

        AccommodationDto expected = new AccommodationDto(
                updatedAccommodation.getId(),
                updatedAccommodation.getType(),
                new LocationDto(location.getId(),
                        location.getCountry(),
                        location.getCity(),
                        location.getRegion(),
                        location.getZipCode(),
                        location.getAddress(),
                        location.getDescription()),
                updatedAccommodation.getSize(),
                Set.of(new AmenityDto(1L, amenity.getTitle(), amenity.getDescription())),
                updatedAccommodation.getDailyRate(),
                updatedAccommodation.getAvailability());

        Mockito.when(accommodationRepo.findById(accommodationId))
                .thenReturn(Optional.of(oldAccommodation));
        Mockito.when(locationRepo.findByCountryAndCityAndRegionAndAddressAllIgnoreCase(
                requestDto.location().country(),
                requestDto.location().city(),
                requestDto.location().region(),
                requestDto.location().address())).thenReturn(Optional.of(location));
        Mockito.when(amenityMapper.toModel(amenityRequestDto)).thenReturn(amenity);
        Mockito.when(amenityRepo.findByTitleContainsIgnoreCase(amenity.getTitle()))
                .thenReturn(Optional.of(amenity));
        Mockito.when(amenityRepo.save(amenity)).thenReturn(amenity);
        Mockito.lenient().when(accommodationMapper.updateAccommodationFromDto(
                updatedAccommodation, requestDto))
                .thenReturn(updatedAccommodation);
        Mockito.when(accommodationMapper.updateAccommodationFromDto(oldAccommodation, requestDto))
                        .thenReturn(updatedAccommodation);
        Mockito.when(accommodationRepo.save(updatedAccommodation)).thenReturn(updatedAccommodation);
        Mockito.when(accommodationMapper.toDto(updatedAccommodation)).thenReturn(expected);

        //When
        AccommodationDto actual = accommodationService.updateAccommodationById(
                accommodationId, requestDto);

        //Then
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expected, actual));
    }
}
