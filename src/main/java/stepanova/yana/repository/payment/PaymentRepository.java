package stepanova.yana.repository.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import stepanova.yana.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}
