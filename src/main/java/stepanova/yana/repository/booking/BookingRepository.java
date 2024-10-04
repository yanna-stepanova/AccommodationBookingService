package stepanova.yana.repository.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import stepanova.yana.model.Booking;
import stepanova.yana.model.Status;

import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    /*@Query("""
            select b from Booking b
            where b.accommodation.id = :accommodationId and b.status <> :status
            and :fromDate between b.checkInDate and (b.checkOutDate - 1)
            or (b.checkInDate < :toDate and b.checkOutDate >= :toDate)""")*/
    @Query(value = "SELECT * FROM bookings b WHERE b.accommodation_id = :accommodationId "
            + "AND :fromDate BETWEEN b.check_in_date AND (b.check_out_date - 1) "
            + "or (b.check_in_date < :toDate and b.check_out_date >= :toDate)", nativeQuery = true)
    List<Booking> findAllByAccommodationIdAndFromDateAndToDate(
            @Param("accommodationId") Long accommodationId,
          //  @Param("status") Status status,
            @Param("fromDate") LocalDate checkInDate,
            @Param("toDate") LocalDate checkOutDate);
}
