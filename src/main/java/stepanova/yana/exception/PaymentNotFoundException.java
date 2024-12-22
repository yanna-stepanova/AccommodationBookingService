package stepanova.yana.exception;

import jakarta.persistence.EntityNotFoundException;

public class PaymentNotFoundException extends EntityNotFoundException {
    public PaymentNotFoundException(String message) {
        super(message);
    }
}
