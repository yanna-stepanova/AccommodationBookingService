package stepanova.yana.util;

import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;
import stepanova.yana.dto.accommodation.AccommodationDto;
import stepanova.yana.dto.accommodation.CreateAccommodationRequestDto;
import stepanova.yana.dto.accommodation.UpdateAccommodationRequestDto;
import stepanova.yana.dto.accommodation.UpdateAllAccommodationRequestDto;
import stepanova.yana.dto.amenity.CreateAmenityRequestDto;
import stepanova.yana.dto.booking.CreateBookingRequestDto;
import stepanova.yana.dto.location.CreateLocationRequestDto;
import stepanova.yana.dto.location.LocationDto;
import stepanova.yana.dto.user.UserProfileRequestDto;
import stepanova.yana.dto.user.UserRegistrationRequestDto;
import stepanova.yana.model.Accommodation;
import stepanova.yana.model.Amenity;
import stepanova.yana.model.Booking;
import stepanova.yana.model.Location;
import stepanova.yana.model.Role;
import stepanova.yana.model.RoleName;
import stepanova.yana.model.Status;
import stepanova.yana.model.Type;
import stepanova.yana.model.User;

public class DataFactoryForServices {
    public static CreateAccommodationRequestDto createValidAccommodationRequestDto() {
        return new CreateAccommodationRequestDto(
                "Hostel",
                new CreateLocationRequestDto("Ukraine", "Rivne", "Rivne region",
                        null,"Kyivska street, 10", null),
                "cupsule room (6 places)",
                Set.of(), BigDecimal.valueOf(7), 6);
    }

    public static AccommodationDto createExpectedAccommodationDtoForSaving(
            Accommodation accommodation, Location location) {
        return new AccommodationDto(5L,
                accommodation.getType(),
                new LocationDto(7L, location.getCountry(), location.getCity(), location.getRegion(),
                        location.getZipCode(), location.getAddress(), location.getDescription()),
                accommodation.getSize(), Set.of(),
                accommodation.getDailyRate(),
                accommodation.getAvailability());
    }

    public static Accommodation createValidAccommodation(Long id) {
        Accommodation accommodation = new Accommodation();
        accommodation.setId(id);
        accommodation.setType(Type.HOSTEL);
        accommodation.setLocation(new Location());
        accommodation.setAmenities(Set.of());
        accommodation.setSize("cupsule room (6 places)");
        accommodation.setDailyRate(BigDecimal.valueOf(7));
        accommodation.setAvailability(6);
        return accommodation;
    }

    public static Accommodation createFirstAccommodation() {
        Accommodation accommodation = new Accommodation();
        accommodation.setId(1L);
        accommodation.setType(Type.HOSTEL);
        accommodation.setLocation(new Location());
        accommodation.setAmenities(Set.of());
        accommodation.setSize("cupsule room (6 places)");
        accommodation.setDailyRate(BigDecimal.valueOf(7));
        accommodation.setAvailability(6);
        return accommodation;
    }

    public static Accommodation createSecondAccommodation() {
        Accommodation accommodation = new Accommodation();
        accommodation.setId(2L);
        accommodation.setType(Type.HOTEL);
        accommodation.setLocation(new Location());
        accommodation.setAmenities(Set.of());
        accommodation.setSize("1 bedroom");
        accommodation.setDailyRate(BigDecimal.valueOf(35));
        accommodation.setAvailability(10);
        return accommodation;
    }

    public static UpdateAccommodationRequestDto createUpdateAccommodationRequestDto() {
        return new UpdateAccommodationRequestDto(
                "hotel", "1 double room", BigDecimal.valueOf(16), 3);
    }

    public static CreateAmenityRequestDto createValidAmenityRequestDto() {
        return new CreateAmenityRequestDto("TV", "Televition");
    }

    public static UpdateAllAccommodationRequestDto createUpdateAllAccommodationRequestDto(
            CreateAmenityRequestDto amenityRequestDto) {
        return new UpdateAllAccommodationRequestDto(
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
    }

    public static Amenity createValidAmenity() {
        Amenity amenity = new Amenity();
        amenity.setTitle("TV");
        amenity.setDescription("Televition");
        return amenity;
    }

    public static Role createValidCustomerRole() {
        Role role = new Role();
        role.setName(RoleName.CUSTOMER);
        return role;
    }

    public static Role createValidCustomerRole(Long id) {
        Role role = new Role();
        role.setId(id);
        role.setName(RoleName.CUSTOMER);
        return role;
    }

    public static Role createValidAdminRole(Long id) {
        Role role = new Role();
        role.setId(id);
        role.setName(RoleName.ADMIN);
        return role;
    }

    public static User createValidUser(Role role) {
        User user = new User();
        user.setId(1L);
        user.setEmail("userw@gmail.com");
        user.setFirstName("Person");
        user.setLastName("Default");
        user.setPassword("password");
        user.setRole(role);
        return user;
    }

    public static User createValidUserWithIdAndRole(Long id, Role role) {
        User user = new User();
        user.setId(id);
        user.setEmail("default@gmail.com");
        user.setFirstName("Name");
        user.setLastName("Surname");
        user.setPassword("password");
        user.setRole(role);
        return user;
    }

    public static CreateBookingRequestDto createValidBookingRequestDto() {
        return new CreateBookingRequestDto(
                LocalDate.of(2024, 9, 9),
                LocalDate.of(2024, 9, 11), 1L);
    }

    public static Booking createValidBooking(User user, Status status) {
        Booking booking = new Booking();
        booking.setId(10L);
        booking.setUser(user);
        booking.setStatus(status);
        booking.setAccommodation(new Accommodation(3L));
        booking.setCheckInDate(LocalDate.of(2024, 12,5));
        booking.setCheckOutDate(LocalDate.of(2024, 12, 10));
        return booking;
    }

    public static Session createValidSession() {
        Session session = new Session();
        session.setId("1111");
        session.setUrl("https://checkout.stripe.com/c/pay/cs_test_a11E");
        return session;
    }

    public static UserRegistrationRequestDto createUserRegistrationRequestDto() {
        return new UserRegistrationRequestDto(
                "user@email.com",
                "UserName",
                "UserSurname",
                "password",
                "password");
    }

    public static UserProfileRequestDto createUserProfileRequestDto() {
        return new UserProfileRequestDto("Taras", "Schevchenko");
    }
}
