package stepanova.yana.service.impl;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionUpdateParams;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import stepanova.yana.model.Booking;
import stepanova.yana.model.Status;
import stepanova.yana.service.StripeService;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class StripeServiceImpl implements StripeService {
    private static final String CURRENCY = "UAH";
    private static final String HTTP = "http";
    private static final String HOST = "localhost:8080";
    private static final String PATH = "/api/payments/";

    @Value("${stripe.test.key}")
    private String stripeTestKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeTestKey;
    }

    @Override
    public Customer createCustomer(String customerEmail, String customerName) throws StripeException {
        CustomerCreateParams params = CustomerCreateParams.builder()
                .setEmail(customerEmail)
                .setName(customerName)
                .build();
        return Customer.create(params);
    }

    @Override
    public Session createPaymentSession(Booking booking, BigDecimal amountToPay) throws StripeException {
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
                                .setUnitAmountDecimal(amountToPay)
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
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
        session.setPaymentStatus(Status.PAID.getStatusName());
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
