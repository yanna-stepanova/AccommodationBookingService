package stepanova.yana.repository.payment;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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
import stepanova.yana.model.Payment;
import stepanova.yana.model.Status;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class PaymentRepositoryTest {
    @Autowired
    private PaymentRepository paymentRepo;

    @BeforeAll
    public static void beforeAll(@Autowired DataSource dataSource) throws SQLException {
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(
                    "database/payment/repository/add-all-tables-for-payments.sql"));
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
                    "database/clear-all-tables.sql"));
        }
    }

    @Test
    @DisplayName("""
            Find one payment with status not 'CANCELED' and not 'PAID'
            between 13.11.2024 - 14.11.2024""")
    void findAllByStatusNotInAndDateBetween_WithStatusNotCanceledAndPaidInPeriod_ReturnOne() {
        LocalDateTime fromDateTime = LocalDateTime.of(
                LocalDate.of(2024, 11, 13), LocalTime.of(0, 0, 0));
        LocalDateTime toDateTime = LocalDateTime.of(
                LocalDate.of(2024, 11, 14), LocalTime.of(23, 59, 59));
        List<Payment> actual = paymentRepo.findAllByStatusNotInAndDateBetween(
                Set.of(Status.CANCELED.getStatusName(), Status.PAID.getStatusName()),
                fromDateTime, toDateTime);
        Assertions.assertEquals(1, actual.size());
    }

    @Test
    @DisplayName("""
            Find zero payment with status not 'CANCELED' and not 'PAID' for a day 15.11.2024""")
    void findAllByStatusNotInAndDateBetween_WithStatusNotCanceledAndPaidForDay_ReturnZero() {
        LocalDateTime fromDateTime = LocalDateTime.of(
                LocalDate.of(2024, 11, 15), LocalTime.of(0, 0, 0));
        LocalDateTime toDateTime = LocalDateTime.of(
                LocalDate.of(2024, 11, 15), LocalTime.of(23, 59, 59));
        List<Payment> actual = paymentRepo.findAllByStatusNotInAndDateBetween(
                Set.of(Status.CANCELED.getStatusName(), Status.PAID.getStatusName()),
                fromDateTime, toDateTime);
        Assertions.assertEquals(0, actual.size());
    }

    @Test
    @DisplayName("""
            Find a payment by existing booking id""")
    void findByBookingId_WithExistingBookingId_Ok() {
        Optional<Payment> actual = paymentRepo.findByBookingId(2L);
        Assertions.assertTrue(actual.isPresent());
    }

    @Test
    @DisplayName("""
            Find a payment by non-existing booking id""")
    void findByBookingId_WithNonExistingBookingId_NotOk() {
        Optional<Payment> actual = paymentRepo.findByBookingId(20L);
        Assertions.assertFalse(actual.isPresent());
    }

    @Test
    @DisplayName("""
            Find a payment by existing session id and matching user id""")
    void findBySessionIdAndUserId_WithExistingSessionIdAndMatchingUserId_Ok() {
        String sessionId = "cs_test_a2gb3y8A2jIUfj3rWUr6hIHtDUkKy78Ab48NHhDBZ8J5010f1GOKbwjTsT";
        Optional<Payment> actual = paymentRepo.findBySessionIdAndUserId(sessionId, 2L);
        Assertions.assertTrue(actual.isPresent());
    }

    @Test
    @DisplayName("""
            Find a payment by existing session id and not matching user id""")
    void findBySessionIdAndUserId_WithExistingSessionIdAndNotMatchingUserId_Ok() {
        String sessionId = "cs_test_a2gb3y8A2jIUfj3rWUr6hIHtDUkKy78Ab48NHhDBZ8J5010f1GOKbwjTsT";
        Optional<Payment> actual = paymentRepo.findBySessionIdAndUserId(sessionId, 1L);
        Assertions.assertFalse(actual.isPresent());
    }

    @Test
    @DisplayName("""
            Find a payment by non-existing session id and non-existing user id""")
    void findBySessionIdAndUserId_WithNonExistingSessionIdAndNonExistingUserId_Ok() {
        String sessionId = "cs_test_a100gb3y8A2jIUfj3rWUr6hIHtDUkKy78Ab48NHhDBZ8J5010f1GOKbwjTsT";
        Optional<Payment> actual = paymentRepo.findBySessionIdAndUserId(sessionId, 100L);
        Assertions.assertFalse(actual.isPresent());
    }

    @Test
    @DisplayName("""
            Find three payments by existing user id""")
    void findAllByUserId_WithExistingUserId_ReturnThree() {
        List<Payment> actual = paymentRepo.findAllByUserId(2L);
        Assertions.assertEquals(3, actual.size());
    }

    @Test
    @DisplayName("""
            Find zero payments by existing user id""")
    void findAllByUserId_WithExistingUserId_ReturnZero() {
        List<Payment> actual = paymentRepo.findAllByUserId(1L);
        Assertions.assertEquals(0, actual.size());
    }

    @Test
    @DisplayName("""
            Find zero payments by non-existing user id""")
    void findAllByUserId_WithNonExistingUserId_ReturnZero() {
        List<Payment> actual = paymentRepo.findAllByUserId(100L);
        Assertions.assertEquals(0, actual.size());
    }
}
