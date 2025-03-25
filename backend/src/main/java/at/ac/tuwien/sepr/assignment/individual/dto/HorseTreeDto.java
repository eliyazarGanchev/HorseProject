package at.ac.tuwien.sepr.assignment.individual.dto;

import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import java.time.LocalDate;

/**
 * A Data Transfer Object representing a horse in a genealogical tree structure.
 * This record stores essential horse details such as ID, name, date of birth, and sex,
 * along with optional references to its mother and father (both also represented as
 * {@code HorseTreeDto} instances).
 *
 * @param id           The unique identifier of the horse.
 * @param name         The horse's name.
 * @param dateOfBirth  The date the horse was born.
 * @param sex          The biological sex of the horse.
 * @param mother       The mother horse, represented as a {@code HorseTreeDto}.
 * @param father       The father horse, represented as a {@code HorseTreeDto}.
 */
public record HorseTreeDto(
        Long id,
        String name,
        LocalDate dateOfBirth,
        Sex sex,
        HorseTreeDto mother,
        HorseTreeDto father
) {

}
