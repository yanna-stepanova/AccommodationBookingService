package stepanova.yana.service;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import stepanova.yana.model.Booking;

import java.math.BigDecimal;

public interface StripeService {
    Customer createCustomer(String customerEmail, String customerName) throws StripeException;

    Session createPaymentSession(Booking booking, BigDecimal amountToPay) throws StripeException;

    Session getSession(String sessionId) throws StripeException;
}
