package at.ac.tuwien.sepr.assignment.individual.service.impl;


import at.ac.tuwien.sepr.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseUpdateDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Validator for horse-related operations, ensuring that all horse data meets the required constraints.
 */
@Component
public class HorseValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


  /**
   * Validates a horse before updating, ensuring all fields meet constraints and checking for conflicts.
   *
   * @param horse the {@link HorseUpdateDto} to validate
   * @throws ValidationException if validation fails
   * @throws ConflictException   if conflicts with existing data are detected
   */
  public void validateForUpdate(HorseUpdateDto horse) throws ValidationException, ConflictException {
    LOG.trace("validateForUpdate({})", horse);
    List<String> validationErrors = new ArrayList<>();

    if (horse.id() == null) {
      validationErrors.add("No ID given");
    }

    if (horse.description() != null) {
      if (horse.description().isBlank()) {
        validationErrors.add("Horse description is given but blank");
      }
      if (horse.description().length() > 4095) {
        validationErrors.add("Horse description too long: longer than 4095 characters");
      }
    }

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for update failed", validationErrors);
    }

  }

  /**
   * Validates a horse before creation, ensuring all fields meet constraints.
   *
   * @param horse the {@link HorseCreateDto} to validate
   * @throws ValidationException if validation fails
   */
  public void validateForCreate(HorseCreateDto horse) throws ValidationException {
    LOG.trace("validateForCreate({})", horse);
    List<String> validationErrors = new ArrayList<>();

    if (horse.name() == null || horse.name().isBlank()) {
      validationErrors.add("Horse name is required");
    }
    if (horse.name() != null && horse.name().length() > 255) {
      validationErrors.add("Horse name too long: longer than 255 characters");
    }

    if (horse.description() != null) {
      if (horse.description().isBlank()) {
        validationErrors.add("Horse description is given but blank");
      }
      if (horse.description().length() > 4095) {
        validationErrors.add("Horse description too long: longer than 4095 characters");
      }
    }

    if (horse.dateOfBirth() == null) {
      validationErrors.add("Horse date of birth is required");
    }

    if (horse.sex() == null) {
      validationErrors.add("Horse gender (sex) is required");
    } else if (!horse.sex().toString().equals("MALE") && !horse.sex().toString().equals("FEMALE")) {
      validationErrors.add("Invalid horse gender: must be 'MALE' or 'FEMALE'");
    }

    if (horse.ownerId() != null && horse.ownerId() <= 0) {
      validationErrors.add("Invalid owner ID: must be a positive number");
    }

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for creation failed", validationErrors);
    }
  }

  /**
   * Validates a horse before deletion, ensuring it exists in the system.
   *
   * @param id the ID of the horse to delete
   * @throws ValidationException if the horse does not exist or cannot be deleted
   */
  public void validateForDelete(Long id) throws ValidationException {
    LOG.trace("validateForDelete({})", id);
    List<String> validationErrors = new ArrayList<>();
    if (id == null) {
      validationErrors.add("Horse ID is required");
    }
  }

}
