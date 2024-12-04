package stepanova.yana.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stepanova.yana.dto.accommodation.AccommodationDto;
import stepanova.yana.dto.accommodation.AccommodationDtoWithoutLocationAndAmenities;
import stepanova.yana.dto.accommodation.CreateAccommodationRequestDto;
import stepanova.yana.dto.accommodation.UpdateAccommodationRequestDto;
import stepanova.yana.dto.accommodation.UpdateAllAccommodationRequestDto;
import stepanova.yana.service.AccommodationService;

@Tag(name = "Accommodation manager", description = "Endpoints for managing accommodations")
@RequiredArgsConstructor
@RestController
@RequestMapping("/accommodations")
@Validated
public class AccommodationController {
    private final AccommodationService accommodationService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping
    @Operation(summary = "Create a new accommodation",
            description = "Create a new accommodation entity in the database")
    public AccommodationDto createAccommodation(
            @RequestBody @Valid CreateAccommodationRequestDto requestDto) {
        return accommodationService.save(requestDto);
    }

    @GetMapping
    @Operation(summary = "Get all accommodations in parts",
            description = "Get all the accommodations in parts + using sorting")
    public List<AccommodationDtoWithoutLocationAndAmenities> getAllAccommodation(
            @ParameterObject @PageableDefault Pageable pageable) {
        return accommodationService.getAll(pageable);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get an accommodation by id",
            description = "Get an accommodation entity by id from the database")
    public AccommodationDto getAccommodation(@PathVariable @Positive Long id) {
        return accommodationService.getAccommodationById(id);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Update an accommodation by id",
            description = "Update just accommodation details")
    public AccommodationDto updateAccommodation(
            @PathVariable @Positive Long id,
            @RequestBody @Valid UpdateAccommodationRequestDto newRequestDto) {
        return accommodationService.updateAccommodationById(id, newRequestDto);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PutMapping("/{id}/all")
    @Operation(summary = "Update an accommodation by id with attached information",
            description = "Update all information of accommodation (location + amenities)")
    public AccommodationDto updateAccommodation(
            @PathVariable @Positive Long id,
            @RequestBody @Valid UpdateAllAccommodationRequestDto newRequestDto) {
        return accommodationService.updateAccommodationById(id, newRequestDto);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a book by id",
            description = "Delete a book by id (not physically - just mark it as deleted)")
    public String deleteAccommodation(@PathVariable @Positive Long id) {
        accommodationService.deleteById(id);
        return "The accommodation entity was deleted by id: " + id;
    }
}
