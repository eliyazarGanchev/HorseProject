package at.ac.tuwien.sepr.assignment.individual.dto;

import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import java.time.LocalDate;

public record HorseTreeDto(
        Long id,
        String name,
        LocalDate dateOfBirth,
        Sex sex,
        HorseTreeDto mother,
        HorseTreeDto father
) {

}
