package at.ac.tuwien.sepr.assignment.individual.dto;

import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import java.time.LocalDate;

/**
 * Represents a Data Transfer Object (DTO) for loading a list of horses.
 * This record encapsulates essential horse attributes required for listing.
 *
 * @param id            the unique identifier of the horse.
 * @param name          the name of the horse.
 * @param description   a brief description or additional information about the horse.
 * @param dateOfBirth   the birthdate of the horse.
 * @param sex           the biological sex of the horse.
 * @param owner         horse's owner.
 */
public record HorseListDto(
    Long id,
    String name,
    String description,
    LocalDate dateOfBirth,
    Sex sex,
    OwnerDto owner
) {
}
