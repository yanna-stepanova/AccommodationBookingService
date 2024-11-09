package stepanova.yana.repository.accommodation.location;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import javax.sql.DataSource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import stepanova.yana.model.Location;
import stepanova.yana.repository.accommodation.LocationRepository;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LocationRepositoryTest {
    @Autowired
    private LocationRepository locationRepo;

    @BeforeAll
    public static void beforeAll(@Autowired DataSource dataSource) throws SQLException {
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(
                    "database/accommodation/add-locations.sql"));
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
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(
                    "database/accommodation/clear-all-dependent-tables.sql"));
        }
    }

    @Test
    @DisplayName("Find location by existing country/city/region/address ")
    void findByCountryAndCityAndRegionAndAddressAllIgnoreCase_ExistingLocation_Ok() {
        Optional<Location> actual = locationRepo
                .findByCountryAndCityAndRegionAndAddressAllIgnoreCase(
                "Ukraine", "Novy Yar", "Lviv region", "Yavorian Lake, SIRKA SPORT");
        Assertions.assertTrue(actual.isPresent());
    }

    @Test
    @DisplayName("Find location by non-existing country/city/region/address ")
    void findByCountryAndCityAndRegionAndAddressAllIgnoreCase_NonExistingLocation_NotOk() {
        Optional<Location> actual = locationRepo
                .findByCountryAndCityAndRegionAndAddressAllIgnoreCase(
                "Ukraine", "Kharkiv", "Kharkiv region", "Alchevskyh street, 10/12");
        Assertions.assertFalse(actual.isPresent());
    }

    @Test
    @DisplayName("Find location by existing location but different city(not all data matches)")
    void findByCountryAndCityAndRegionAndAddressAllIgnoreCase_CityNonMatches_NotOk() {
        Optional<Location> actual = locationRepo
                .findByCountryAndCityAndRegionAndAddressAllIgnoreCase(
                "Ukraine", "Kharkiv", "Kharkiv region", "Alchevskyh street, 10/12");
        Assertions.assertFalse(actual.isPresent());
    }

    @Test
    @DisplayName("Find location by existing data in upper case")
    void findByCountryAndCityAndRegionAndAddressAllIgnoreCase_DataInUpperCase_Ok() {
        Optional<Location> actual = locationRepo
                .findByCountryAndCityAndRegionAndAddressAllIgnoreCase(
                "UKRAINE", "NOVY YAR", "LVIV REGION", "YAVORIAN LAKE, SIRKA SPORT");
        Assertions.assertTrue(actual.isPresent());
    }

    @Test
    @DisplayName("Find location by existing data in lower case")
    void findByCountryAndCityAndRegionAndAddressAllIgnoreCase_DataInLowerCase_Ok() {
        Optional<Location> actual = locationRepo
                .findByCountryAndCityAndRegionAndAddressAllIgnoreCase(
                "ukraine", "novy yar", "lviv region", "yavorian lake, sirka sport");
        Assertions.assertTrue(actual.isPresent());
    }

    @Test
    @DisplayName("Find location by existing data in different case")
    void findByCountryAndCityAndRegionAndAddressAllIgnoreCase_DataInMixCase_Ok() {
        Optional<Location> actual = locationRepo
                .findByCountryAndCityAndRegionAndAddressAllIgnoreCase(
                "UkrAInE", "NoVy yaR", "lViV rEgIoN", "yavorian LAKE, siRKa spOrt");
        Assertions.assertTrue(actual.isPresent());
    }
}
