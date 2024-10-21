package stepanova.yana.repository.payment;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import stepanova.yana.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByBookingId(Long id);

    @Query(value = "SELECT p.id, p.status, p.date_time_created, p.booking_id, p.amount_to_pay, "
            + "p.session_url, p.session_id, p.is_deleted FROM payments p LEFT JOIN bookings b "
            + "ON p.booking_id = b.id WHERE p.session_id = :sessionId AND b.user_id = :userId ",
            nativeQuery = true)
    Optional<Payment> findBySessionIdAndUserId(@Param("sessionId") String sessionId,
                                               @Param("userId") Long userId);

    @Query(value = "SELECT p.id, p.status, p.date_time_created, p.booking_id, p.amount_to_pay, "
            + "p.session_url, p.session_id, p.is_deleted FROM payments p LEFT JOIN bookings b "
            + "ON p.booking_id = b.id WHERE b.user_id = :userId",
            nativeQuery = true)
    List<Payment> findAllByUserId(@Param("userId") Long userId);
}
