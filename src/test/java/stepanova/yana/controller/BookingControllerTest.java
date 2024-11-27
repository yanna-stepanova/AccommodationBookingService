package stepanova.yana.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import stepanova.yana.dto.accommodation.AccommodationDtoWithoutAvailability;
import stepanova.yana.dto.amenity.AmenityDto;
import stepanova.yana.dto.booking.BookingDto;
import stepanova.yana.dto.booking.BookingDtoWithoutDetails;
import stepanova.yana.dto.booking.CreateBookingRequestDto;
import stepanova.yana.dto.booking.UpdateBookingStatusRequestDto;
import stepanova.yana.dto.location.LocationDto;
import stepanova.yana.dto.user.UserResponseDto;
import stepanova.yana.model.Status;
import stepanova.yana.model.Type;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BookingControllerTest {
    protected static MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired DataSource dataSource,
                          @Autowired WebApplicationContext context) throws SQLException {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/booking/controller/"
                            + "add-all-tables-for-booking.sql"));
        }
    }

    @AfterAll
    public static void afterAll(@Autowired DataSource dataSource) {
        teardown(dataSource);
    }

    @SneakyThrows
    public static void teardown(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection,
                    new ClassPathResource("database/clear-all-tables.sql"));
        }
    }

    @Test
    @DisplayName("Create a new booking of some accommodation")
    @WithUserDetails("user@example.com")
    @Sql(scripts = "classpath:database/booking/controller/remove-third-booking.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createBooking_GivenValidUserAndRequestDto_Success() throws Exception {
        //Given
        CreateBookingRequestDto requestDto = new CreateBookingRequestDto(LocalDate.now(),
                LocalDate.now().plusDays(1L), 1L);

        //When
        MvcResult result = mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        //Then
        BookingDto expected = new BookingDto(3L,
                requestDto.checkInDate(),
                requestDto.checkOutDate(),
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
        BookingDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookingDto.class);

        Assertions.assertNotNull(actual);
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expected, actual,
                "user","accommodation"));
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(
                expected.getAccommodation(), actual.getAccommodation(),
                "location", "amenities"));
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(
                expected.getAccommodation().getLocation(),
                actual.getAccommodation().getLocation()));
        Assertions.assertEquals(expected.getAccommodation().getAmenities().size(),
                actual.getAccommodation().getAmenities().size());
        for (AmenityDto amenityExpected : expected.getAccommodation().getAmenities()) {
            for (AmenityDto amenityActual : actual.getAccommodation().getAmenities()) {
                if (amenityExpected.getId().equals(amenityActual.getId())) {
                    Assertions.assertTrue(EqualsBuilder.reflectionEquals(amenityExpected,
                            amenityActual));
                }
            }
        }
    }

    @Test
    @DisplayName("Get all bookings for some user and by some status")
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void getBookingsByUserAndStatus_GivenValidUserIdAndStatus_Success() throws Exception {
        //Given
        Long userId = 2L;
        String statusName = "canceled";

        //When
        MvcResult result = mockMvc.perform(get("/bookings")
                        .param("userId", String.valueOf(userId))
                        .param("statusName", statusName)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        //Then
        BookingDtoWithoutDetails bookingDto = new BookingDtoWithoutDetails(1L,
                LocalDate.of(2024, 11, 13),
                LocalDate.of(2024, 11, 14),
                1L,2L, Status.CANCELED);
        List<BookingDtoWithoutDetails> expected = List.of(bookingDto);

        BookingDtoWithoutDetails[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookingDtoWithoutDetails[].class);
        Assertions.assertEquals(expected.size(), actual.length);
        for (int i = 0; i < expected.size(); i++) {
            Assertions.assertTrue(EqualsBuilder.reflectionEquals(
                    expected.get(i), actual[i]));
        }
    }

    @Test
    @DisplayName("Get all user's bookings")
    @WithUserDetails("user@example.com")
    void getBookingsForOwner_GivenValidUser_Success() throws Exception {
        //Given & When
        MvcResult result = mockMvc.perform(get("/bookings/my")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        //Then
        BookingDtoWithoutDetails bookingDtoOne = new BookingDtoWithoutDetails(1L,
                LocalDate.of(2024, 11, 13),
                LocalDate.of(2024, 11, 14),
                1L,2L, Status.CANCELED);
        BookingDtoWithoutDetails bookingDtoTwo = new BookingDtoWithoutDetails(2L,
                LocalDate.of(2023, 8, 30),
                LocalDate.of(2023, 9, 2),
                2L,2L, Status.PENDING);
        List<BookingDtoWithoutDetails> expected = List.of(bookingDtoOne, bookingDtoTwo);

        BookingDtoWithoutDetails[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookingDtoWithoutDetails[].class);
        Assertions.assertEquals(expected.size(), actual.length);
        for (int i = 0; i < expected.size(); i++) {
            Assertions.assertTrue(EqualsBuilder.reflectionEquals(
                    expected.get(i), actual[i]));
        }
    }

    @Test
    @DisplayName("Get a booking by id for user")
    @WithUserDetails("user@example.com")
    void getBookingForOwner_GivenValidUserAndBookingId_Success() throws Exception {
        //Given
        Long bookingId = 1L;

        //When
        MvcResult result = mockMvc.perform(get("/bookings/{id}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        //Then
        BookingDto expected = new BookingDto(bookingId,
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

        BookingDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookingDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expected, actual,
                "user","accommodation"));
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(
                expected.getAccommodation(), actual.getAccommodation(),
                "location", "amenities"));
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(
                expected.getAccommodation().getLocation(),
                actual.getAccommodation().getLocation()));
        Assertions.assertEquals(expected.getAccommodation().getAmenities().size(),
                actual.getAccommodation().getAmenities().size());
        for (AmenityDto amenityExpected : expected.getAccommodation().getAmenities()) {
            for (AmenityDto amenityActual : actual.getAccommodation().getAmenities()) {
                if (amenityExpected.getId().equals(amenityActual.getId())) {
                    Assertions.assertTrue(EqualsBuilder.reflectionEquals(amenityExpected,
                            amenityActual));
                }
            }
        }
    }

    @Test
    @DisplayName("Get a booking by id for admin")
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    void getBooking_GivenValidBookingId_Success() throws Exception {
        //Given
        Long bookingId = 1L;

        ///When
        MvcResult result = mockMvc.perform(get("/bookings/{id}/extra", bookingId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        //Then
        BookingDto expected = new BookingDto(bookingId,
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

        BookingDto actual = objectMapper.readValue(result.getResponse().getContentAsString(),
                BookingDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expected, actual,
                "user","accommodation"));
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(
                expected.getAccommodation(), actual.getAccommodation(),
                "location", "amenities"));
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(
                expected.getAccommodation().getLocation(),
                actual.getAccommodation().getLocation()));
        Assertions.assertEquals(expected.getAccommodation().getAmenities().size(),
                actual.getAccommodation().getAmenities().size());
        for (AmenityDto amenityExpected : expected.getAccommodation().getAmenities()) {
            for (AmenityDto amenityActual : actual.getAccommodation().getAmenities()) {
                if (amenityExpected.getId().equals(amenityActual.getId())) {
                    Assertions.assertTrue(EqualsBuilder.reflectionEquals(amenityExpected,
                            amenityActual));
                }
            }
        }
    }

    @Test
    @DisplayName("Update a booking by id")
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Sql(scripts = "classpath:database/booking/controller/add-one-booking.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/booking/controller/remove-third-booking.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateBooking_GivenValidBookingIdAndRequestDto_Success() throws Exception {
        //Given
        Long bookingId = 3L;
        UpdateBookingStatusRequestDto requestDto = new UpdateBookingStatusRequestDto("paid");

        //When
        MvcResult result = mockMvc.perform(put("/bookings/{id}", bookingId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();
        //Then
        BookingDto expected = new BookingDto(bookingId,
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

        BookingDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookingDto.class);

        Assertions.assertNotNull(actual);
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expected, actual,
                "user","accommodation"));
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(
                expected.getAccommodation(), actual.getAccommodation(),
                "location", "amenities"));
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(
                expected.getAccommodation().getLocation(),
                actual.getAccommodation().getLocation()));
        Assertions.assertEquals(expected.getAccommodation().getAmenities().size(),
                actual.getAccommodation().getAmenities().size());
        for (AmenityDto amenityExpected : expected.getAccommodation().getAmenities()) {
            for (AmenityDto amenityActual : actual.getAccommodation().getAmenities()) {
                if (amenityExpected.getId().equals(amenityActual.getId())) {
                    Assertions.assertTrue(EqualsBuilder.reflectionEquals(amenityExpected,
                            amenityActual));
                }
            }
        }
    }

    @Test
    @DisplayName("Cancel a booking by id for owner")
    @WithUserDetails("user@example.com")
    @Sql(scripts = "classpath:database/booking/controller/add-one-booking.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/booking/controller/remove-third-booking.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void deleteBooking_GivenValidBookingIdAndUser_Success() throws Exception {
        //Given
        Long bookingId = 3L;

        //When
        MvcResult result = mockMvc.perform(delete("/bookings/{id}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        //Then
        BookingDto expected = new BookingDto(bookingId,
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

        BookingDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), BookingDto.class);

        Assertions.assertNotNull(actual);
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expected, actual,
                "user","accommodation"));
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(
                expected.getAccommodation(), actual.getAccommodation(),
                "location", "amenities"));
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(
                expected.getAccommodation().getLocation(),
                actual.getAccommodation().getLocation()));
        Assertions.assertEquals(expected.getAccommodation().getAmenities().size(),
                actual.getAccommodation().getAmenities().size());
        for (AmenityDto amenityExpected : expected.getAccommodation().getAmenities()) {
            for (AmenityDto amenityActual : actual.getAccommodation().getAmenities()) {
                if (amenityExpected.getId().equals(amenityActual.getId())) {
                    Assertions.assertTrue(EqualsBuilder.reflectionEquals(amenityExpected,
                            amenityActual));
                }
            }
        }
    }
}
