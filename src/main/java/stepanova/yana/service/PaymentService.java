package stepanova.yana.service;

import stepanova.yana.dto.payment.CreatePaymentRequestDto;
import stepanova.yana.dto.payment.PaymentDto;

public interface PaymentService {
    PaymentDto save(Long userId, CreatePaymentRequestDto requestDto);
}
