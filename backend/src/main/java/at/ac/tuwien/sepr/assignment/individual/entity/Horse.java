package at.ac.tuwien.sepr.assignment.individual.entity;

import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import java.time.LocalDate;

/**
 * Represents a horse entity in the persistent data store.
 * This record encapsulates all relevant attributes of a horse,
 * including personal details, ownership, lineage, and optional media information.
 *
 * @param id            the unique identifier of the horse.
 * @param name          the name of the horse.
 * @param description   a brief description or additional information about the horse.
 * @param dateOfBirth   the birthdate of the horse.
 * @param sex           the biological sex of the horse.
 * @param ownerId       the unique identifier of the horse's owner.
 * @param motherId      the unique identifier of the horse's mother (may be null if unknown).
 * @param fatherId      the unique identifier of the horse's father (may be null if unknown).
 * @param image         the binary image data representing the horse (optional, may be null).
 * @param imageType     the MIME type of the image (optional, e.g., "image/png").
 */
public record Horse(
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
