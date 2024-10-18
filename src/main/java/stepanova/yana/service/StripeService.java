package stepanova.yana.service;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import stepanova.yana.model.Booking;

import java.math.BigDecimal;

public interface StripeService {

    Session createSession(Booking booking, BigDecimal amountToPay) throws StripeException;
}
