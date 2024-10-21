package stepanova.yana.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import stepanova.yana.config.MapperConfig;
import stepanova.yana.dto.payment.PaymentDto;
import stepanova.yana.model.Payment;

@Mapper(config = MapperConfig.class, uses = BookingMapper.class)
public interface PaymentMapper {
    @Mapping(source = "booking.id", target = "bookingId")
    @Mapping(source = "booking.accommodation", target = "accommodation")
    PaymentDto toDto(Payment payment);
}
