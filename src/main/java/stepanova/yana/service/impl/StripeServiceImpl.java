package stepanova.yana.service.impl;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import stepanova.yana.model.Booking;
import stepanova.yana.service.StripeService;
import stepanova.yana.util.StripeClient;

@RequiredArgsConstructor
@Service
public class StripeServiceImpl implements StripeService {
    private final StripeClient stripeClient;

    @Override
    public Session createPaymentSession(Booking booking, BigDecimal amountToPay)
            throws StripeException {
        return stripeClient.createPaymentSession(booking, amountToPay);
    }

    @Override
    public Session getSession(String sessionId) throws StripeException {
        return stripeClient.getSession(sessionId);
    }
}
