package stepanova.yana.repository.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import stepanova.yana.model.Booking;
import stepanova.yana.model.Status;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query(value = "SELECT * FROM bookings b WHERE b.accommodation_id = :accommodationId "
            + "AND b.status <> :statusName "
            + "AND (:fromDate BETWEEN b.check_in_date AND (b.check_out_date - 1) "
            + "or :toDate BETWEEN (b.check_in_date + 1) and b.check_out_date)", nativeQuery = true)
    List<Booking> findAllByAccommodationIdAndStatusAndFromDateAndToDate(
            @Param("accommodationId") Long accommodationId,
            @Param("statusName") String statusName,
            @Param("fromDate") LocalDate checkInDate,
            @Param("toDate") LocalDate checkOutDate);
}
