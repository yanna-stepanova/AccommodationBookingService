package stepanova.yana.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stepanova.yana.dto.accommodation.AccommodationDto;
import stepanova.yana.dto.accommodation.CreateAccommodationRequestDto;
import stepanova.yana.service.AccommodationService;

@Tag(name = "Accomodation manager", description = "Endpoints for managing accommodations")
@RequiredArgsConstructor
@RestController
@RequestMapping("/accommodations")
@Validated
public class AccommodationController {
    private final AccommodationService accommodationService;

    @PostMapping
    @Operation(summary = "Create a new accommodation",
            description = "Create a new accommodation entity in the database")
    public AccommodationDto createAccommodation(@RequestBody @Valid CreateAccommodationRequestDto requestDto) {
        return accommodationService.save(requestDto);
    }

    @GetMapping
    @Operation(summary = "Get all accommodations in parts",
            description = "Get all the accommodations in parts + using sorting")
    public List<AccommodationDto> getAll() {
        return accommodationService.getAll();
    }
}
