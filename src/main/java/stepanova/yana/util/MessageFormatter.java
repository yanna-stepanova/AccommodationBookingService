package stepanova.yana.util;

import java.util.stream.Collectors;
import stepanova.yana.model.Accommodation;
import stepanova.yana.model.Amenity;
import stepanova.yana.model.Booking;
import stepanova.yana.model.Payment;

public class MessageFormatter {
    public static String formatBookingMessage(Booking booking, String action) {
        return String.format("%s booking!!!%n"
                        + " id: %s%n"
                        + " status: %s%n"
                        + " check in: %s%n"
                        + " check out: %s%n"
                        + " accommodation id: %s%n"
                        + " user id: %s",
                action,
                booking.getId(),
                booking.getStatus(),
                booking.getCheckInDate(),
                booking.getCheckOutDate(),
                booking.getAccommodation().getId(),
                booking.getUser().getId());
    }

    public static String formatPaymentMessage(Payment payment, String action) {
        return String.format("%s payment!!!%n"
                        + " id: %s%n"
                        + " status: %s%n"
                        + " created date: %s%n"
                        + " booking id: %s%n"
                        + " accommodation id: %s%n"
                        + " amount: %s USD%n"
                        + " session id: %s%n"
                        + " session url: %s",
                action,
                payment.getId(),
                payment.getStatus(),
                payment.getDateTimeCreated(),
                payment.getBooking().getId(),
                payment.getBooking().getAccommodation().getId(),
                payment.getAmountToPay(),
                payment.getSessionID(),
                payment.getSessionUrl());
    }

    public static String formatAccommodationMessage(Accommodation accommodation, String action) {
        return String.format("%s accommodation!!!%n"
                        + " id: %s%n"
                        + " type: %s%n"
                        + " size: %s%n"
                        + " daily rate: %s%n"
                        + " quantity: %s%n"
                        + " amenities: %s%n"
                        + " Location id: %s%n"
                        + "     country: %s%n"
                        + "     city: %s%n"
                        + "     region: %s%n"
                        + "     zipCode: %s%n"
                        + "     address: %s%n"
                        + "     description: %s%n",
                action,
                accommodation.getId(),
                accommodation.getType(),
                accommodation.getSize(),
                accommodation.getDailyRate(),
                accommodation.getAvailability(),
                accommodation.getAmenities().stream()
                        .map(Amenity::getTitle)
                        .collect(Collectors.joining(", ")),
                accommodation.getLocation().getId(),
                accommodation.getLocation().getCountry(),
                accommodation.getLocation().getCity(),
                accommodation.getLocation().getRegion(),
                accommodation.getLocation().getZipCode(),
                accommodation.getLocation().getAddress(),
                accommodation.getLocation().getDescription());
    }
}

