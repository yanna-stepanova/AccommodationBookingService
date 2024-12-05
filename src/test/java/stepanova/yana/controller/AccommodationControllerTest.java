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
import java.util.ArrayList;
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
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import stepanova.yana.dto.accommodation.AccommodationDto;
import stepanova.yana.dto.accommodation.AccommodationDtoWithoutLocationAndAmenities;
import stepanova.yana.dto.accommodation.CreateAccommodationRequestDto;
import stepanova.yana.dto.accommodation.UpdateAccommodationRequestDto;
import stepanova.yana.dto.amenity.AmenityDto;
import stepanova.yana.dto.location.CreateLocationRequestDto;
import stepanova.yana.dto.location.LocationDto;
import stepanova.yana.model.Type;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccommodationControllerTest {
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
                    new ClassPathResource("database/accommodation/controller/"
                            + "add-data-in-tables-for-accommodation.sql"));
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
                    new ClassPathResource("database/accommodation/controller/"
                            + "clear-all-data.sql"));
        }
    }

    @Test
    @DisplayName("Create a new accommodation")
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Sql(scripts = "classpath:database/accommodation/controller/"
            + "remove-accommodation-and-location.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void createAccommodation_WithValidRequestDto_Success() throws Exception {
        //Given
        CreateAccommodationRequestDto requestDto = new CreateAccommodationRequestDto(
                "house",
                new CreateLocationRequestDto("Ukraine", "Sumy", "Sumy region",
                        null, "street Lushpy 7", null),
                "1 bedroom", Set.of(), BigDecimal.valueOf(20L), 1);

        LocationDto locationDto = new LocationDto();
        locationDto.setCountry(requestDto.location().country());
        locationDto.setCity(requestDto.location().city());
        locationDto.setRegion(requestDto.location().region());
        locationDto.setAddress(requestDto.location().address());

        AccommodationDto expected = new AccommodationDto();
        expected.setType(Type.getByType(requestDto.typeName()));
        expected.setLocation(locationDto);
        expected.setSize(requestDto.size());
        expected.setAmenities(Set.of());
        expected.setDailyRate(requestDto.dailyRate());
        expected.setAvailability(requestDto.availability());

        //When
        MvcResult result = mockMvc.perform(post("/accommodations")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        //Then
        AccommodationDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), AccommodationDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertNotNull(actual.getId());
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(
                expected.getLocation(), actual.getLocation(), "id"));
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(
                expected, actual, "id", "location"));
    }

    @Test
    @DisplayName("Get all accommodations")
    void getAllAccommodation_GivenAccommodationsList_ReturnThree() throws Exception {
        //Given
        List<AccommodationDtoWithoutLocationAndAmenities> expected = getAllAccommodationsDto();

        //When
        MvcResult result = mockMvc.perform(get("/accommodations")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        //Then
        AccommodationDtoWithoutLocationAndAmenities[] actual = objectMapper.readValue(
                result.getResponse().getContentAsString(),
                AccommodationDtoWithoutLocationAndAmenities[].class);
        Assertions.assertEquals(expected.size(), actual.length);
        for (int i = 0; i < expected.size(); i++) {
            Assertions.assertTrue(EqualsBuilder.reflectionEquals(
                    expected.get(i), actual[i]));
        }
    }

    @Test
    @DisplayName("Get accommodation by valid id")
    @WithMockUser(username = "admin", authorities = {"ADMIN", "CUSTOMER"})
    void getAccommodation_WithValidId_Success() throws Exception {
        //Given
        Long accommodationId = 2L;

        //When
        MvcResult result = mockMvc.perform(get("/accommodations/{id}", accommodationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        //Then
        AccommodationDto expected = new AccommodationDto(2L,Type.HOTEL,
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

        AccommodationDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), AccommodationDto.class);
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expected, actual,
                "location", "amenities"));
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expected.getLocation(),
                actual.getLocation()));
        for (AmenityDto amenityExpected : expected.getAmenities()) {
            for (AmenityDto amenityActual : actual.getAmenities()) {
                if (amenityExpected.getId().equals(amenityActual.getId())) {
                    Assertions.assertTrue(EqualsBuilder.reflectionEquals(amenityExpected,
                            amenityActual));
                }
            }
        }
    }

    @Test
    @DisplayName("Update accommodation by valid id and requestDto")
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Sql(scripts = "classpath:database/accommodation/controller/add-accommodation-for-updating.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/accommodation/controller/remove-updated-accommodation.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void updateAccommodation_GivenValidIdAndRequestDto_Success() throws Exception {
        //Given
        Long accommodationId = 4L;
        UpdateAccommodationRequestDto requestDto = new UpdateAccommodationRequestDto("hostel",
                "6th capsul bedroom", BigDecimal.valueOf(9.35), 6);
        AccommodationDto expected = new AccommodationDto(accommodationId,
                Type.getByType(requestDto.typeName()),
                new LocationDto(2L, "Ukraine", "Skhidnytsia", "Lviv region",
                        "82391", "Boryslavska Street, 81",
                        "cell: +38(097)7715102, https://www.lubo-kray.com.ua"),
                requestDto.size(), Set.of(), requestDto.dailyRate(), requestDto.availability());

        //When
        MvcResult result = mockMvc.perform(put("/accommodations/{id}", accommodationId)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        //Then
        AccommodationDto actual = objectMapper.readValue(
                result.getResponse().getContentAsString(), AccommodationDto.class);
        Assertions.assertNotNull(actual);
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expected, actual, "location"));
        Assertions.assertTrue(EqualsBuilder.reflectionEquals(expected.getLocation(),
                actual.getLocation()));
    }

    @Test
    @DisplayName("Delete accommodation by valid id")
    @WithMockUser(username = "admin", authorities = {"ADMIN"})
    @Sql(scripts = "classpath:database/accommodation/controller/add-accommodation-for-updating.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void deleteAccommodation_GivenValidId_Success() throws Exception {
        //Given
        Long accommodationId = 4L;

        //When & Then
        mockMvc.perform(delete("/accommodations/{id}", accommodationId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is2xxSuccessful())
                .andExpect(result -> Assertions.assertEquals(
                        "The accommodation entity was deleted by id: " + accommodationId,
                        result.getResponse().getContentAsString()));
    }

    private List<AccommodationDtoWithoutLocationAndAmenities> getAllAccommodationsDto() {
        List<AccommodationDtoWithoutLocationAndAmenities> dtoList = new ArrayList<>();
        dtoList.add(new AccommodationDtoWithoutLocationAndAmenities(
                1L, Type.VACATION_HOME, "1 Bedroom", BigDecimal.valueOf(100.05), 2));
        dtoList.add(new AccommodationDtoWithoutLocationAndAmenities(
                2L, Type.HOTEL, "apartment-room", BigDecimal.valueOf(98.05), 5));
        dtoList.add(new AccommodationDtoWithoutLocationAndAmenities(
                3L, Type.HOTEL, "mansard-room", BigDecimal.valueOf(80.05), 8));
        return dtoList;
    }
}