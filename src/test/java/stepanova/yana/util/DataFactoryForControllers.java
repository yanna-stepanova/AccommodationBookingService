package stepanova.yana.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import stepanova.yana.dto.accommodation.AccommodationDto;
import stepanova.yana.dto.accommodation.AccommodationDtoWithoutAvailability;
import stepanova.yana.dto.accommodation.AccommodationDtoWithoutLocationAndAmenities;
import stepanova.yana.dto.accommodation.CreateAccommodationRequestDto;
import stepanova.yana.dto.accommodation.UpdateAccommodationRequestDto;
import stepanova.yana.dto.amenity.AmenityDto;
import stepanova.yana.dto.booking.BookingDto;
import stepanova.yana.dto.booking.BookingDtoWithoutDetails;
import stepanova.yana.dto.booking.CreateBookingRequestDto;
import stepanova.yana.dto.location.CreateLocationRequestDto;
import stepanova.yana.dto.location.LocationDto;
import stepanova.yana.dto.payment.PaymentDto;
import stepanova.yana.dto.user.UserRegistrationRequestDto;
import stepanova.yana.dto.user.UserResponseDto;
import stepanova.yana.model.RoleName;
import stepanova.yana.model.Status;
import stepanova.yana.model.Type;

public class DataFactoryForControllers {
    public static CreateAccommodationRequestDto createValidAccommodationRequestDto() {
        return new CreateAccommodationRequestDto(
                "house",
                new CreateLocationRequestDto("Ukraine", "Sumy", "Sumy region",
                        null, "street Lushpy 7", null),
                "1 bedroom", Set.of(), BigDecimal.valueOf(20L), 1
        );
    }

    public static List<AccommodationDtoWithoutLocationAndAmenities> getAllAccommodationsDto() {
        List<AccommodationDtoWithoutLocationAndAmenities> dtoList = new ArrayList<>();
        dtoList.add(new AccommodationDtoWithoutLocationAndAmenities(
                1L, Type.VACATION_HOME, "1 Bedroom", BigDecimal.valueOf(100.05), 2));
        dtoList.add(new AccommodationDtoWithoutLocationAndAmenities(
                2L, Type.HOTEL, "apartment-room", BigDecimal.valueOf(98.05), 5));
        dtoList.add(new AccommodationDtoWithoutLocationAndAmenities(
                3L, Type.HOTEL, "mansard-room", BigDecimal.valueOf(80.05), 8));
        return dtoList;
    }

    public static AccommodationDto expectedAccommodationDtoForGettingById() {
        return new AccommodationDto(2L,Type.HOTEL,
                new LocationDto(2L, "Ukraine", "Skhidnytsia", "Lviv region",
                        "82391", "Boryslavska Street, 81",
                        "cell: +38(097)7715102, https://www.lubo-kray.com.ua"),
                "apartment-room",
                Set.of(new AmenityDto(1L, "WiFi", "WiFi is free"),
                        new AmenityDto(3L, "Hairdryer", null),
                        new AmenityDto(4L, "Swimming pool",
                                "Indoor pool with sauna and Finnish bath"),
                        new AmenityDto(5L, "Parking(unsecured)", "Free parking")),
                BigDecimal.valueOf(98.05), 5);
    }

    public static UpdateAccommodationRequestDto createUpdateAccommodationRequestDto() {
        return new UpdateAccommodationRequestDto("hostel",
                "6th capsul bedroom", BigDecimal.valueOf(9.35), 6);
    }

    public static AccommodationDto expectedAccommodationDtoForUpdatingById(Long id) {
        return new AccommodationDto(id,
                Type.valueOf("HOSTEL"),
                new LocationDto(2L, "Ukraine", "Skhidnytsia", "Lviv region",
                        "82391", "Boryslavska Street, 81",
                        "cell: +38(097)7715102, https://www.lubo-kray.com.ua"),
                "6th capsul bedroom", Set.of(), BigDecimal.valueOf(9.35), 6);
    }

    public static UserRegistrationRequestDto createValidUserRegistrationRequestDto() {
        return new UserRegistrationRequestDto("nobode@example.com",
                "Person", "Nobody", "person1234", "person1234");
    }

    public static CreateBookingRequestDto createValidBookingRequestDto() {
        return new CreateBookingRequestDto(LocalDate.now(),
                LocalDate.now().plusDays(1L), 1L);
    }

    public static BookingDto createExpectedBookingDto(LocalDate dateIn, LocalDate dateOut) {
        return new BookingDto(3L,
                dateIn,
                dateOut,
                new AccommodationDtoWithoutAvailability(1L, Type.VACATION_HOME,
                        new LocationDto(1L, "Ukraine", "Novy Yar",
                                "Lviv region", "81050",
                                "Yavorian Lake, SIRKA SPORT",
                                "cell: +38(073)8761234, "
                                        + "https://sirka.ua, Google Maps(49.951422, 23.488195)"),
                        "1 Bedroom",
                        Set.of(new AmenityDto(1L, "WiFi", "WiFi is free"),
                                new AmenityDto(2L, "Parking(secured)", "Private parking"),
                                new AmenityDto(3L, "Hairdryer", null)),
                        BigDecimal.valueOf(100.01)),
                new UserResponseDto(2L,
                        "user@example.com",
                        "Someone",
                        "Person",
                        "CUSTOMER"),
                Status.PENDING);
    }

    public static BookingDto createExpectedBookingDto(Long id) {
        return new BookingDto(id,
                LocalDate.of(2024, 11, 13),
                LocalDate.of(2024, 11, 14),
                new AccommodationDtoWithoutAvailability(1L, Type.VACATION_HOME,
                        new LocationDto(1L, "Ukraine", "Novy Yar",
                                "Lviv region", "81050",
                                "Yavorian Lake, SIRKA SPORT",
                                "cell: +38(073)8761234, "
                                        + "https://sirka.ua, Google Maps(49.951422, 23.488195)"),
                        "1 Bedroom",
                        Set.of(new AmenityDto(1L, "WiFi", "WiFi is free"),
                                new AmenityDto(2L, "Parking(secured)", "Private parking"),
                                new AmenityDto(3L, "Hairdryer", null)),
                        BigDecimal.valueOf(100.01)),
                new UserResponseDto(2L,
                        "user@example.com",
                        "Someone",
                        "Person",
                        "CUSTOMER"),
                Status.CANCELED);
    }

    public static BookingDtoWithoutDetails createValidBookingDtoWithoutDetails() {
        return new BookingDtoWithoutDetails(1L,
                LocalDate.of(2024, 11, 13),
                LocalDate.of(2024, 11, 14),
                1L,2L, Status.CANCELED);
    }

    public static List<BookingDtoWithoutDetails> getListOfTwoBookingDtoWitoutDetails() {
        return List.of(
                new BookingDtoWithoutDetails(1L,
                LocalDate.of(2024, 11, 13),
                LocalDate.of(2024, 11, 14),
                1L,2L, Status.CANCELED),
                new BookingDtoWithoutDetails(2L,
                LocalDate.of(2023, 8, 30),
                LocalDate.of(2023, 9, 2),
                2L,2L, Status.PENDING));
    }

    public static BookingDto createExpectedBookingDtoForGettingById(Long id) {
        return new BookingDto(id,
                LocalDate.of(2024, 11, 13),
                LocalDate.of(2024, 11, 14),
                new AccommodationDtoWithoutAvailability(1L, Type.VACATION_HOME,
                        new LocationDto(1L, "Ukraine", "Novy Yar",
                                "Lviv region", "81050",
                                "Yavorian Lake, SIRKA SPORT",
                                "cell: +38(073)8761234, "
                                        + "https://sirka.ua, Google Maps(49.951422, 23.488195)"),
                        "1 Bedroom",
                        Set.of(new AmenityDto(1L, "WiFi", "WiFi is free"),
                                new AmenityDto(2L, "Parking(secured)", "Private parking"),
                                new AmenityDto(3L, "Hairdryer", null)),
                        BigDecimal.valueOf(100.01)),
                new UserResponseDto(2L,
                        "user@example.com",
                        "Someone",
                        "Person",
                        "CUSTOMER"),
                Status.CANCELED);
    }

    public static BookingDto createExpectedBookingDtoForUpdatingById(Long id) {
        return new BookingDto(id,
                LocalDate.of(2024, 12, 11),
                LocalDate.of(2024, 12, 14),
                new AccommodationDtoWithoutAvailability(2L,Type.HOTEL,
                        new LocationDto(2L, "Ukraine", "Skhidnytsia", "Lviv region",
                                "82391", "Boryslavska Street, 81",
                                "cell: +38(097)7715102, https://www.lubo-kray.com.ua"),
                        "apartment-room",
                        Set.of(new AmenityDto(1L, "WiFi", "WiFi is free"),
                                new AmenityDto(3L, "Hairdryer", null),
                                new AmenityDto(4L, "Swimming pool",
                                        "Indoor pool with sauna and Finnish bath"),
                                new AmenityDto(5L, "Parking(unsecured)", "Free parking")),
                        BigDecimal.valueOf(98.02)),
                new UserResponseDto(2L,
                        "user@example.com",
                        "Someone",
                        "Person",
                        "CUSTOMER"),
                Status.PAID);
    }

    public static BookingDto createExpectedBookingDtoForDeletingById(Long id) {
        return new BookingDto(id,
                LocalDate.of(2024, 12, 11),
                LocalDate.of(2024, 12, 14),
                new AccommodationDtoWithoutAvailability(2L,Type.HOTEL,
                        new LocationDto(2L, "Ukraine", "Skhidnytsia", "Lviv region",
                                "82391", "Boryslavska Street, 81",
                                "cell: +38(097)7715102, https://www.lubo-kray.com.ua"),
                        "apartment-room",
                        Set.of(new AmenityDto(1L, "WiFi", "WiFi is free"),
                                new AmenityDto(3L, "Hairdryer", null),
                                new AmenityDto(4L, "Swimming pool",
                                        "Indoor pool with sauna and Finnish bath"),
                                new AmenityDto(5L, "Parking(unsecured)", "Free parking")),
                        BigDecimal.valueOf(98.02)),
                new UserResponseDto(2L,
                        "user@example.com",
                        "Someone",
                        "Person",
                        "CUSTOMER"),
                Status.CANCELED);
    }

    public static PaymentDto createExpectedPendingPaymentDto(Long id) {
        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setStatus(Status.PENDING);
        paymentDto.setBookingId(id);
        paymentDto.setAmountToPay(BigDecimal.valueOf(98.05));
        return paymentDto;
    }

    public static PaymentDto createExpectedPaidPaymentDto(String sessionId) {
        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setId(10L);
        paymentDto.setBookingId(1L);
        paymentDto.setStatus(Status.PAID);
        paymentDto.setSessionID(sessionId);
        paymentDto.setAmountToPay(BigDecimal.valueOf(100.05));
        return paymentDto;
    }

    public static List<PaymentDto> getListOfOnePaymentDto() {
        PaymentDto paymentDto = new PaymentDto();
        paymentDto.setId(10L);
        paymentDto.setBookingId(1L);
        paymentDto.setStatus(Status.PENDING);
        paymentDto.setAmountToPay(BigDecimal.valueOf(100.05));
        return List.of(paymentDto);
    }

    public static UserResponseDto createExpectedUserResponseDto() {
        return new UserResponseDto(2L,
                "user@gmail.com", "Username", "UserSurname",
                RoleName.CUSTOMER.getRoleName());
    }

    public static UserResponseDto createUpdatedRoleUserResponseDto(Long id) {
        return new UserResponseDto(id,
                "newAdmin@gmail.com", "New_admin", "employee",
                RoleName.ADMIN.getRoleName());
    }

    public static UserResponseDto createUpdatedProfileUserResponseDto() {
        return new UserResponseDto(3L,
                "newAdmin@gmail.com", "Alice", "Alison",
                RoleName.CUSTOMER.getRoleName());
    }
}
