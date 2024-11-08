package stepanova.yana.repository.user;

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
import org.springframework.security.core.userdetails.UserDetails;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {
    @Autowired
    private UserRepository userRepo;

    @BeforeAll
    public static void beforeAll(@Autowired DataSource dataSource) throws SQLException {
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(
                    "database/user/add-roles-and-users.sql"));
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
                    "database/user/clear-all-dependent-tabled.sql"));
        }
    }

    @Test
    @DisplayName("Check the registered email exists in DB")
    void existsByEmail_RegisteredEmail_Ok() {
        String email = "user@gmail.com";
        boolean actual = userRepo.existsByEmail(email);
        Assertions.assertTrue(actual);
    }

    @Test
    @DisplayName("Check the email doesn't exist in DB")
    void existsByEmail_NonRegisteredEmail_NotOk() {
        String email = "user24@example.com";
        boolean actual = userRepo.existsByEmail(email);
        Assertions.assertFalse(actual);
    }

    @Test
    @DisplayName("Find user by existing email")
    void findByEmail_ValidEmail_Ok() {
        Optional<UserDetails> actual = userRepo.findByEmail("user@gmail.com");
        Assertions.assertTrue(actual.isPresent());
    }
    @Test
    @DisplayName("Find user by non-existing email")
    void findByEmail_NonExistingEmail_NotOk() {
        Optional<UserDetails> actual = userRepo.findByEmail("customer@gmail.com");
        Assertions.assertFalse(actual.isPresent());
    }
}
