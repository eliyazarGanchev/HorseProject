package at.ac.tuwien.sepr.assignment.individual.dto;

import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import java.time.LocalDate;

/**
 * REST-DTO for updating horses.
 * Contains the same fields as the normal update DTO, without the ID (which should come from the request URL instead)
 *
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
public record HorseUpdateRestDto(
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

  /**
   * Converts the current object into a {@link HorseUpdateDto}, associating it with the specified identifier.
   * This method is typically used to prepare update operations by combining existing horse data with an explicit ID.
   *
   * @param id the identifier of the horse to associate with the DTO
   * @return a new {@code HorseUpdateDto} instance populated with the current object's data and the provided ID
   */
  public HorseUpdateDto toUpdateDtoWithId(Long id) {
    return new HorseUpdateDto(id, name, description, dateOfBirth, sex, ownerId, motherId, fatherId, image, imageType);
  }

}
