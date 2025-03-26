package at.ac.tuwien.sepr.assignment.individual.dto;

import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * DTO to bundle the query parameters used in searching horses.
 * Each field can be null, in which case this field is not filtered by.
 *
 * @param name          the name of the horse.
 * @param description   a brief description or additional information about the horse.
 * @param dateOfBirth   the birthdate of the horse.
 * @param sex           the biological sex of the horse.
 * @param ownerName         horse's owner.
 */
public record HorseSearchDto(
    String name,
    String description,
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    LocalDate dateOfBirth,
    Sex sex,
    String ownerName
) {
}
