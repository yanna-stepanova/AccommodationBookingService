package stepanova.yana.util;

import java.util.stream.Collectors;
import stepanova.yana.dto.accommodation.AccommodationDto;
import stepanova.yana.dto.amenity.AmenityDto;
import stepanova.yana.dto.booking.BookingDto;
import stepanova.yana.dto.payment.PaymentDto;

public class MessageFormatter {
    public static String formatBookingMessage(BookingDto bookingDto, String action) {
        return String.format("%s booking!!!%n"
                        + " id: %s%n"
                        + " status: %s%n"
                        + " check in: %s%n"
                        + " check out: %s%n"
                        + " accommodation id: %s%n"
                        + " user id: %s",
                action,
                bookingDto.getId(),
                bookingDto.getStatus(),
                bookingDto.getCheckInDate(),
                bookingDto.getCheckOutDate(),
                bookingDto.getAccommodation().getId(),
                bookingDto.getUser().getId());
    }

    public static String formatPaymentMessage(PaymentDto paymentDto, String action) {
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
                paymentDto.getId(),
                paymentDto.getStatus(),
                paymentDto.getDateTimeCreated(),
                paymentDto.getBookingId(),
                paymentDto.getAccommodation().getId(),
                paymentDto.getAmountToPay(),
                paymentDto.getSessionID(),
                paymentDto.getSessionUrl());
    }

    public static String formatAccommodationMessage(AccommodationDto accommodation, String action) {
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
                        .map(AmenityDto::getTitle)
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

