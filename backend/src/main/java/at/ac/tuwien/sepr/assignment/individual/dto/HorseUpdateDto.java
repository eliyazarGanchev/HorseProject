package at.ac.tuwien.sepr.assignment.individual.dto;

import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import java.time.LocalDate;

/**
 * Represents a Data Transfer Object (DTO) for updating horse details.
 * This record encapsulates all necessary fields for updating a horse entry.
 *
 * @param id            the unique identifier of the horse.
 * @param name          the name of the horse.
 * @param description   a brief description or additional information about the horse.
 * @param dateOfBirth   the birthdate of the horse.
 * @param sex           the biological sex of the horse.
 * @param ownerId       the identifier of the horse's owner.
 * @param motherId      the identifier of the horse's mother (optional, may be null).
 * @param fatherId      the identifier of the horse's father (optional, may be null).
 * @param image         a byte array representing the image of the horse (optional, may be null).
 * @param imageType     the MIME type of the provided horse image (optional, e.g., "image/png").
 */
public record HorseUpdateDto(
    Long id,
    String name,
    String description,
    LocalDate dateOfBirth,
    Sex sex,
    Long ownerId,
    Long motherId,
    Long fatherId,
    byte[] image,
    String imageType
) {

}
