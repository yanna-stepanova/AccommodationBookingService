package stepanova.yana.service;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import stepanova.yana.model.Booking;

public interface StripeService {
    Session createPaymentSession(Booking booking, BigDecimal amountToPay) throws StripeException;

    Session getSession(String sessionId) throws StripeException;
}
