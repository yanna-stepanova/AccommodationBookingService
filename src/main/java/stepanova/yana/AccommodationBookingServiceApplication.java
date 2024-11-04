package stepanova.yana;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class AccommodationBookingServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccommodationBookingServiceApplication.class, args);
    }

}
