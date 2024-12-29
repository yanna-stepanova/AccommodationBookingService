package stepanova.yana.controller;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
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
import stepanova.yana.dto.amenity.AmenityDto;
import stepanova.yana.dto.booking.BookingDto;
import stepanova.yana.dto.booking.BookingDtoWithoutDetails;
import stepanova.yana.dto.booking.CreateBookingRequestDto;
import stepanova.yana.dto.booking.UpdateBookingStatusRequestDto;
import stepanova.yana.util.TestDataFactory;

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
        CreateBookingRequestDto requestDto = TestDataFactory.createValidBookingRequestDto();

        //When
        MvcResult result = mockMvc.perform(post("/bookings")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        //Then
        BookingDto expected = TestDataFactory.createExpectedBookingDto(requestDto.checkInDate(),
                requestDto.checkOutDate());
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
                .andExpect(status().isOk())
                .andReturn();

        //Then
        BookingDtoWithoutDetails bookingDto = TestDataFactory.createValidBookingDtoWithoutDetails();
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
                .andExpect(status().isOk())
                .andReturn();

        //Then
        List<BookingDtoWithoutDetails> expected = TestDataFactory.getListOfTwoBookingDtoWitoutDetails();
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
                .andExpect(status().isOk())
                .andReturn();

        //Then
        BookingDto expected = TestDataFactory.createExpectedBookingDto(bookingId);

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
                .andExpect(status().isOk())
                .andReturn();
        //Then
        BookingDto expected = TestDataFactory.createExpectedBookingDtoForGettingById(bookingId);
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
                .andExpect(status().isOk())
                .andReturn();
        //Then
        BookingDto expected = TestDataFactory.createExpectedBookingDtoForUpdatingById(bookingId);
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
                .andExpect(status().isOk())
                .andReturn();

        //Then
        BookingDto expected = TestDataFactory.createExpectedBookingDtoForDeletingById(bookingId);
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
