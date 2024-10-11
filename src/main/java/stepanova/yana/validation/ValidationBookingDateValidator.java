package stepanova.yana.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import org.springframework.beans.BeanWrapperImpl;

public class ValidationBookingDateValidator
        implements ConstraintValidator<ValidationBookingDate, Object> {
    private String begin;
    private String end;

    @Override
    public void initialize(ValidationBookingDate constraintAnnotation) {
        this.begin = constraintAnnotation.begin();
        this.end = constraintAnnotation.end();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        Object beginDate = new BeanWrapperImpl(value).getPropertyValue(begin);
        Object endDate = new BeanWrapperImpl(value).getPropertyValue(end);

        if (beginDate == null || endDate == null) {
            return true;
        }

        if (!(beginDate instanceof LocalDate)
                || !(endDate instanceof LocalDate)) {
            throw new IllegalArgumentException(
                    "Illegal method signature, expected two parameters of type LocalDate.");
        }

        return (((LocalDate) beginDate).isEqual(LocalDate.now())
                || ((LocalDate) beginDate).isAfter(LocalDate.now()))
                && ((LocalDate) endDate).isAfter((LocalDate) beginDate);
    }
}
