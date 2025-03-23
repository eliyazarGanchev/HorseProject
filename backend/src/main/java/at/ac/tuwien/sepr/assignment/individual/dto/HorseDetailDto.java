package at.ac.tuwien.sepr.assignment.individual.dto;

import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import java.time.LocalDate;

/**
 * Represents a Data Transfer Object (DTO) for detailed horse information.
 * This record provides all necessary details about a horse.
 */
public record HorseDetailDto(
    Long id,
    String name,
    String description,
    LocalDate dateOfBirth,
    Sex sex,
    OwnerDto owner,
    HorseDetailDto mother,
    HorseDetailDto father,
    byte[] image,
    String image_type
) {
}
