package stepanova.yana.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import stepanova.yana.model.Booking;
import stepanova.yana.service.StripeService;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class StripeServiceImpl implements StripeService {
    @Value("${STRIPE_SECRET_KEY}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    @Override
    public Session createSession(Booking booking, BigDecimal amountToPay) throws StripeException {
        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .putMetadata("bookingId", booking.getId().toString())
                .setCurrency("uah")
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setPrice(amountToPay.toString())
                                .setQuantity(1L)
                                .build())
                .setCustomerEmail(booking.getUser().getEmail())
                .build();
        return Session.create(params);
    }
}
