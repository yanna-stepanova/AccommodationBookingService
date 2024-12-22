package stepanova.yana.exception;

import jakarta.persistence.EntityNotFoundException;

public class AccommodationNotFoundException extends EntityNotFoundException {
    public AccommodationNotFoundException(String message) {
        super(message);
    }
}
