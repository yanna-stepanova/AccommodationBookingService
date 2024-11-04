package stepanova.yana.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import stepanova.yana.model.Booking;
import stepanova.yana.service.StripeService;

@RequiredArgsConstructor
@Service
public class StripeServiceImpl implements StripeService {
    private static final String CURRENCY = "USD";
    private static final String HTTP = "http";
    private static final String HOST = "localhost:8080";
    private static final String PATH = "/api/payments/";
    private static final String STATUS_PAID = "paid";
    @Value("${stripe.secret.key}")
    private String stripeApiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeApiKey;
    }

    @Override
    public Customer createCustomer(String customerEmail, String customerName)
            throws StripeException {
        CustomerCreateParams params = CustomerCreateParams.builder()
                .setEmail(customerEmail)
                .setName(customerName)
                .build();
        return Customer.create(params);
    }

    @Override
    public Session createPaymentSession(Booking booking, BigDecimal amountToPay)
            throws StripeException {
        SessionCreateParams params = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setCustomer(createCustomer(
                        booking.getUser().getEmail(),
                        booking.getUser().getFirstName() + booking.getUser().getLastName())
                        .getId())
                .putMetadata("bookingId", booking.getId().toString())
                .setSuccessUrl(generateUrl("success"))
                .setCancelUrl(generateUrl("cancel"))
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency(CURRENCY)
                                .setUnitAmountDecimal(
                                        amountToPay.multiply(BigDecimal.valueOf(100L)))
                                .setProductData(
                                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName("Booking of accommodation")
                                        .build())
                                .build())
                        .build())
                .build();
        return Session.create(params);
    }

    @Override
    public Session getSession(String sessionId) throws StripeException {
        Session session = Session.retrieve(sessionId);
        session.setPaymentStatus(STATUS_PAID);
        return session;
    }

    private String generateUrl(String resultUrl) {
        UriComponents uriComponents = UriComponentsBuilder.newInstance()
                .scheme(HTTP).host(HOST)
                .path(PATH)
                .pathSegment(resultUrl)
                .build();
        return uriComponents.toString();
    }
}
