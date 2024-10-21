package stepanova.yana.service;

import com.stripe.exception.StripeException;
import stepanova.yana.dto.payment.CreatePaymentRequestDto;
import stepanova.yana.dto.payment.PaymentDto;

import java.net.MalformedURLException;
import java.util.List;

public interface PaymentService {
    PaymentDto save(Long userId, CreatePaymentRequestDto requestDto) throws MalformedURLException, StripeException;

    PaymentDto getSuccess(Long userId, String sessionId) throws StripeException;

    String getCancel(Long userId, String sessionId);

    List<PaymentDto> getAllByUser(Long userId);
}
