package at.ac.tuwien.sepr.assignment.individual.dto;

import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import java.time.LocalDate;

/**
 * Represents a Data Transfer Object (DTO) for detailed horse information.
 * This record provides all necessary details about a horse.
 *
 * @param id            the unique identifier of the horse.
 * @param name          the name of the horse.
 * @param description   a brief description or additional information about the horse.
 * @param dateOfBirth   the birthdate of the horse.
 * @param sex           the biological sex of the horse.
 * @param owner         horse's owner.
 * @param mother        horse's mother (optional, may be null).
 * @param father        horse's father (optional, may be null).
 * @param image         a byte array representing the image of the horse (optional, may be null).
 * @param imageType     the MIME type of the provided horse image (optional, e.g., "image/png").
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
    String imageType
) {
}
