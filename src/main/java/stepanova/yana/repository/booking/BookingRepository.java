package stepanova.yana.repository.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import stepanova.yana.model.Booking;

public interface BookingRepository extends JpaRepository<Booking, Long> {
}
