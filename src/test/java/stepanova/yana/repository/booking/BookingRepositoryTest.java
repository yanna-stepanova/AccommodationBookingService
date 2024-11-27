package stepanova.yana.repository.booking;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
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
import org.springframework.test.context.jdbc.Sql;
import stepanova.yana.model.Booking;
import stepanova.yana.model.Status;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepo;

    @BeforeAll
    public static void beforeAll(@Autowired DataSource dataSource) throws SQLException {
        teardown(dataSource);
        try (Connection connection = dataSource.getConnection()) {
            connection.setAutoCommit(true);
            ScriptUtils.executeSqlScript(connection, new ClassPathResource(
                    "database/booking/repository/add-all-tables-for-booking.sql"));
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
    @DisplayName("Find all bookings by status (not canceled&paid) and on date of check out")
    void findAllByStatusNotInAndCheckOutDateIs_StatusNotInCanceledAndPaidAndCheckOut_ReturnOne() {
        List<Booking> actual = bookingRepo.findAllByStatusNotInAndCheckOutDateIs(
                Status.CANCELED, Status.PAID, LocalDate.parse("2023-09-02"));
        Assertions.assertEquals(1, actual.size());
    }

    @Test
    @DisplayName("Find all bookings by status (not canceled&paid) and on today of check out")
    void findAllByStatusNotInAndCheckOutDateIs_StatusNotInCanceledAndPaidAndOutToday_ReturnZero() {
        List<Booking> actual = bookingRepo.findAllByStatusNotInAndCheckOutDateIs(
                Status.CANCELED, Status.PAID, LocalDate.now());
        Assertions.assertEquals(0, actual.size());
    }

    @Test
    @DisplayName("""
            Find four accommodations that are booked between 13.12.2024 - 15.12.2024""")
    @Sql(scripts = "classpath:database/booking/repository/"
            + "add-six-bookings-for-one-accommodation.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/booking/repository/remove-six-bookings.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByAccommodationIdAndStatusAndFromDateAndToDate_FourBookings_Ok() {
        List<Booking> actual = bookingRepo.findAllByAccommodationIdAndStatusAndFromDateAndToDate(
                2L, Status.CANCELED.getStatusName(),
                LocalDate.of(2024,12, 13),
                LocalDate.of(2024,12, 15));
        Assertions.assertEquals(4, actual.size());
    }

    @Test
    @DisplayName("""
            Find zero accommodations that are booked between 13.12.2024 - 15.12.2024""")
    void findAllByAccommodationIdAndStatusAndFromDateAndToDate_ZeroBookings_Ok() {
        List<Booking> actual = bookingRepo.findAllByAccommodationIdAndStatusAndFromDateAndToDate(
                2L, Status.CANCELED.getStatusName(),
                LocalDate.of(2024,12, 13),
                LocalDate.of(2024,12, 15));
        Assertions.assertEquals(0, actual.size());
    }

    @Test
    @DisplayName("""
            Find one booking for userId = 2 and status = 'CANCELED'""")
    void findAllByUserIdAndStatus_OneCanceledBooking_Ok() {
        List<Booking> actual = bookingRepo.findAllByUserIdAndStatus(2L, Status.CANCELED);
        Assertions.assertEquals(1, actual.size());
    }

    @Test
    @DisplayName("""
            Find five booking for userId = 2""")
    @Sql(scripts = "classpath:database/booking/repository/"
            + "add-six-bookings-for-one-accommodation.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    @Sql(scripts = "classpath:database/booking/repository/remove-six-bookings.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    void findAllByUserId_ExistingUserId_ReturnFiveBooking() {
        List<Booking> actual = bookingRepo.findAllByUserId(2L);
        Assertions.assertEquals(5, actual.size());
    }

    @Test
    @DisplayName("""
            Find zero booking for userId = 10""")
    void findAllByUserId_NonExistingUserId_ReturnFiveBooking() {
        List<Booking> actual = bookingRepo.findAllByUserId(10L);
        Assertions.assertEquals(0, actual.size());
    }

    @Test
    @DisplayName("""
            Find a booking by existing ID and existing user ID(its owner)""")
    void findByIdAndUserId_ExistingBookingIdAndExistingOwnerUserId_Ok() {
        Optional<Booking> actual = bookingRepo.findByIdAndUserId(1L, 2L);
        Assertions.assertTrue(actual.isPresent());
    }

    @Test
    @DisplayName("""
            Find a booking by non-existing ID and existing user""")
    void findByIdAndUserId_NonExistingBookingAndExistingUserId_NotOk() {
        Optional<Booking> actual = bookingRepo.findByIdAndUserId(10L, 2L);
        Assertions.assertFalse(actual.isPresent());
    }

    @Test
    @DisplayName("""
            Find a booking by existing ID and existing user ID(its owner)""")
    void findByIdAndUserId_ExistingBookingIdAndExistingNotOwnerUserId_NotOk() {
        Optional<Booking> actual = bookingRepo.findByIdAndUserId(1L, 1L);
        Assertions.assertFalse(actual.isPresent());
    }
}
