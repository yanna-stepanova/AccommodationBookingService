package stepanova.yana.service;

import com.stripe.exception.StripeException;
import java.net.MalformedURLException;
import java.util.List;
import stepanova.yana.dto.payment.CreatePaymentRequestDto;
import stepanova.yana.dto.payment.PaymentDto;

public interface PaymentService {
    PaymentDto save(Long userId, CreatePaymentRequestDto requestDto)
            throws MalformedURLException, StripeException;

    PaymentDto getSuccess(Long userId, String sessionId) throws StripeException;

    String getCancel(Long userId, String sessionId);

    List<PaymentDto> getAllByUser(Long userId);

    void expiredPayments();
}
