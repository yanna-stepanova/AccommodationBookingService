package stepanova.yana.validation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;


@Constraint(validatedBy = ValidationBookingDateValidator.class)
@Target({TYPE})
@Retention(RUNTIME)
@Documented
public @interface ValidationBookingDate {
    String message() default "The date values don't match!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String begin();

    String end();

    @Target({TYPE})
    @Retention(RUNTIME)
    @interface List {
        ValidationBookingDate[] value();
    }
}
