package stepanova.yana.repository.accommodation;

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
import stepanova.yana.model.Amenity;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class AmenityRepositoryTest {
    @Autowired
    private AmenityRepository amenityRepo;

    @BeforeAll
    public static void beforeAll(@Autowired DataSource dataSource) throws SQLException {
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(
                    "database/accommodation/add-amenities.sql"));
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
    @DisplayName("Find amenity by existing data")
    void findByTitleContainsIgnoreCase_ExistingTitle_Ok() {
        Optional<Amenity> actual = amenityRepo.findByTitleContainsIgnoreCase("WiFi");
        Assertions.assertTrue(actual.isPresent());
    }

    @Test
    @DisplayName("Find amenity by existing data in lower case")
    void findByTitleContainsIgnoreCase_ExistingTitleLowerCase_Ok() {
        Optional<Amenity> actual = amenityRepo.findByTitleContainsIgnoreCase("wifi");
        Assertions.assertTrue(actual.isPresent());
    }

    @Test
    @DisplayName("Find amenity by existing data in upper case")
    void findByTitleContainsIgnoreCase_ExistingTitleUpperCase_Ok() {
        Optional<Amenity> actual = amenityRepo.findByTitleContainsIgnoreCase("WIFI");
        Assertions.assertTrue(actual.isPresent());
    }

    @Test
    @DisplayName("Find amenity by non-existing data")
    void findByTitleContainsIgnoreCase_NonExistingTitle_NotOk() {
        Optional<Amenity> actual = amenityRepo.findByTitleContainsIgnoreCase("air conditioning");
        Assertions.assertFalse(actual.isPresent());
    }
}
