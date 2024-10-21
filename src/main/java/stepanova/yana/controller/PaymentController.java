package stepanova.yana.controller;

import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import stepanova.yana.dto.payment.CreatePaymentRequestDto;
import stepanova.yana.dto.payment.PaymentDto;
import stepanova.yana.dto.user.UserResponseDto;
import stepanova.yana.model.User;
import stepanova.yana.service.PaymentService;
import stepanova.yana.validation.FieldsValueMatch;

import java.net.MalformedURLException;
import java.util.List;

@Tag(name = "Payment manager", description = "Endpoints for managing payments")
@RequiredArgsConstructor
@RestController
@RequestMapping("/payments")
@Validated
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Create payment session",
            description = "Initiates payment sessions for booking transactions(unpaid)")
    public PaymentDto createPayment(@AuthenticationPrincipal User user,
                                    @RequestBody @Valid CreatePaymentRequestDto requestDto) throws MalformedURLException, StripeException {
        return paymentService.save(user.getId(), requestDto);
    }

    @GetMapping(value = "/success", params = {"sessionId"})
    @Operation(summary = "Pay a booking by sessionId",
            description = "Handles successful payment processing through Stripe redirection")
    public PaymentDto getSuccess(@AuthenticationPrincipal User user,
                                 @RequestParam String sessionId) throws StripeException {
        return paymentService.getSuccess(user.getId(), sessionId);
    }

    @GetMapping(value = "/cancel", params = {"sessionId"})
    @Operation(summary = "Cancel payment by sessionId",
            description = "Informs about cancellation of payment session")
    public String getCancel(@AuthenticationPrincipal User user,
                                 @RequestParam String sessionId) {
        return paymentService.getCancel(user.getId(), sessionId);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping(params = {"userId"})
    @Operation(summary = "Get all payments by userId",
            description = "Retrieves payment information for admin.")
    public List<PaymentDto> getAllPaymentByUser(@Positive @RequestParam Long userId) {
        return paymentService.getAllByUser(userId);
    }

    @GetMapping("/me")
    @Operation(summary = "Get all payments for owner",
            description = "Retrieves payment information for users.")
    public List<PaymentDto> getPayments(@AuthenticationPrincipal User user) {
        return paymentService.getAllByUser(user.getId());
    }
}
