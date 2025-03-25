package at.ac.tuwien.sepr.assignment.individual.service.impl;


import at.ac.tuwien.sepr.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseUpdateDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import java.lang.invoke.MethodHandles;
import java.util.ArrayList;
import java.util.List;

import at.ac.tuwien.sepr.assignment.individual.persistence.HorseDao;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * Validator for horse-related operations, ensuring that all horse data meets the required constraints.
 */
@Component
public class HorseValidator {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final HorseDao dao;

    public HorseValidator(HorseDao dao) {
        this.dao = dao;
    }

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

    if (horse.name() == null || horse.name().isBlank()) {
      validationErrors.add("Horse name is required");
    } else {
      if (horse.name().length() > 255) {
        validationErrors.add("Horse name too long: longer than 255 characters");
      }
      String namePattern = "^[A-Z][a-zA-Z-]*$";
      if (!horse.name().matches(namePattern)) {
        validationErrors.add("Horse name must start with a capital letter and contain only letters and suitable characters");
      }
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
    } else {
      if (horse.dateOfBirth().isAfter(java.time.LocalDate.now())) {
        validationErrors.add("Horse date of birth cannot be in the future");
      }
      if (horse.dateOfBirth().isBefore(java.time.LocalDate.now().minusYears(62))) {
        validationErrors.add("Horse date of birth indicates an age over 62 years, which is not possible");
      }
    }

    if (horse.sex() == null) {
      validationErrors.add("Horse gender (sex) is required");
    } else if (!horse.sex().toString().equals("MALE") && !horse.sex().toString().equals("FEMALE")) {
      validationErrors.add("Invalid horse gender: must be 'MALE' or 'FEMALE'");
    }

    if (horse.motherId() != null && horse.motherId().equals(horse.id())) {
      validationErrors.add("A horse cannot be its own mother");
    }
    if (horse.fatherId() != null && horse.fatherId().equals(horse.id())) {
      validationErrors.add("A horse cannot be its own father");
    }

    if (horse.motherId() != null) {
      try {
        Horse mother = dao.getById(horse.motherId());
        if (!mother.dateOfBirth().isBefore(horse.dateOfBirth())) {
          validationErrors.add("Mother's date of birth must be before the horse's date of birth");
        }
        if (mother.sex().equals(Sex.MALE)) {
          validationErrors.add("Mother should be female");
        }
      } catch (NotFoundException e) {
        validationErrors.add("Mother not found");
      }
    }

    if (horse.fatherId() != null) {
      try {
        Horse father = dao.getById(horse.fatherId());
        if (!father.dateOfBirth().isBefore(horse.dateOfBirth())) {
          validationErrors.add("Father's date of birth must be before the horse's date of birth");
        }
        if (father.sex().equals(Sex.FEMALE)) {
          validationErrors.add("Father should be male");
        }
      } catch (NotFoundException e) {
        validationErrors.add("Father not found");
      }
    }

    if (horse.ownerId() != null && horse.ownerId() <= 0) {
      validationErrors.add("Invalid owner ID: must be a positive number");
    }

    try {
      Horse existing = dao.getById(horse.id());
      List<Horse> children = dao.getChildrenByParentId(horse.id());
      if (!children.isEmpty()) {
        if (!existing.sex().equals(horse.sex())) {
          for (Horse child : children) {
            if (child.motherId() != null && child.motherId().equals(horse.id()) && horse.sex() != Sex.FEMALE) {
              validationErrors.add("Cannot change parent's sex to " + horse.sex() +
                      " because horse with ID " + child.id() + " lists this horse as its mother.");
            }
            if (child.fatherId() != null && child.fatherId().equals(horse.id()) && horse.sex() != Sex.MALE) {
              validationErrors.add("Cannot change parent's sex to " + horse.sex() +
                      " because horse with ID " + child.id() + " lists this horse as its father.");
            }
          }
        }
        if (!existing.dateOfBirth().equals(horse.dateOfBirth())) {
          for (Horse child : children) {
            if (!horse.dateOfBirth().isBefore(child.dateOfBirth())) {
              validationErrors.add("Parent's new date of birth must be before the child's date of birth (child ID " + child.id() + ").");
            }
          }
        }
      }
    } catch (NotFoundException e) {
      validationErrors.add("Existing horse record not found for parent's update.");
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
  public void validateForCreate(HorseCreateDto horse) throws ValidationException, ConflictException {
    LOG.trace("validateForCreate({})", horse);
    List<String> validationErrors = new ArrayList<>();

    if (horse.name() == null || horse.name().isBlank()) {
      validationErrors.add("Horse name is required");
    }else {
      if (horse.name().length() > 255) {
        validationErrors.add("Horse name too long: longer than 255 characters");
      }
      String namePattern = "^[A-Z][a-zA-Z-]*$";
      if (!horse.name().matches(namePattern)) {
        validationErrors.add("Horse name must start with a capital letter and contain only letters and suitable characters");
      }
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
    }else{
      if (horse.dateOfBirth().isAfter(java.time.LocalDate.now())) {
        validationErrors.add("Horse date of birth cannot be in the future");
      }
      if (horse.dateOfBirth().isBefore(java.time.LocalDate.now().minusYears(62))) {
        validationErrors.add("Horse date of birth indicates an age over 62 years, which is not allowed");
      }
    }

    if (horse.sex() == null) {
      validationErrors.add("Horse gender (sex) is required");
    } else if (!horse.sex().toString().equals("MALE") && !horse.sex().toString().equals("FEMALE")) {
      validationErrors.add("Invalid horse gender: must be 'MALE' or 'FEMALE'");
    }

    if(horse.motherId() != null) {
        try {
            Horse mother = dao.getById(horse.motherId());
            if(!mother.dateOfBirth().isBefore(horse.dateOfBirth())) {
              validationErrors.add("Mother's date of birth should be before horse's date of birth");
            }
            if(mother.sex().equals(Sex.MALE)){
              validationErrors.add("Mother should be female");
            }
        } catch (NotFoundException e) {
            validationErrors.add("Mother not found");
        }
    }

    if(horse.fatherId() != null) {
      try {
        Horse father = dao.getById(horse.fatherId());
        if(!father.dateOfBirth().isBefore(horse.dateOfBirth())) {
          validationErrors.add("Father's date of birth should be before horse's date of birth");
        }
        if(father.sex().equals(Sex.FEMALE)){
          validationErrors.add("Father should be male");
        }
      } catch (NotFoundException e) {
        validationErrors.add("Father not found");
      }
    }

    if (horse.ownerId() != null && horse.ownerId() <= 0) {
      validationErrors.add("Invalid owner ID: must be a positive number");
    }

    if (!validationErrors.isEmpty()) {
      throw new ValidationException("Validation of horse for creation failed", validationErrors);
    }
  }

  /**
   * Validates the provided generation value for the pedigree.
   * This method checks that the maximum generation value is not negative. The generation value
   * represents the maximum number of ancestor generations to display. If the value is non-null
   * and less than 0, it throws a {@link ValidationException} with an appropriate error message.
   *
   * @param maxGenerations the maximum number of generations to display; may be null, which means no specific limit.
   * @throws ValidationException if {@code maxGenerations} is not null and is less than 0.
   */
  public void validatePedigreeGenerations(Integer maxGenerations) throws ValidationException {
    if (maxGenerations != null && maxGenerations < 0) {
      List<String> validationErrors = new ArrayList<>();
      validationErrors.add("Invalid generation value: " + maxGenerations);
      throw new ValidationException("Generation value must be at least 0", validationErrors);
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
