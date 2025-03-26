package at.ac.tuwien.sepr.assignment.individual.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseTreeDto;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration test for {@link HorseService}.
 */
@ActiveProfiles({"test", "datagen"}) // Enables "test" Spring profile during test execution
@SpringBootTest
public class HorseServiceTest {

  @Autowired
  HorseService horseService;

  // Field to hold the ID of the created horse for cleanup.
  private Long createdHorseId;


  /**
   * Tests whether retrieving all stored horses returns the expected number and specific entries.
   */
  @Test
  public void getAllReturnsAllStoredHorses() {
    List<HorseListDto> horses = horseService.allHorses()
        .toList();

    assertThat(horses.size()).isGreaterThanOrEqualTo(1);

    assertThat(horses)
        .map(HorseListDto::id, HorseListDto::sex)
        .contains(tuple(-1L, Sex.FEMALE));
  }

  /**
   * getById – Positive.
   * Calls getById with a valid, existing horse ID and asserts that:
   * - A valid HorseDetailDto is returned.
   * - Expected fields (id, name, dateOfBirth, etc.) match the test data.
   */
  @Test
  void testServiceGetById_Positive() throws Exception {
    HorseDetailDto horse = horseService.getById(-4);
    assertAll(() -> assertThat(horse).isNotNull(),
            () -> assertThat(horse.id()).isEqualTo(-4L),
            () -> assertThat(horse.name()).containsIgnoringCase("Daisy"),
            () -> assertThat(horse.dateOfBirth()).isNotNull());
  }

  /**
   * getById – Negative.
   * Calls getById with a non-existent ID and expects a NotFoundException.
   */
  @Test
  void testServiceGetById_Negative() {
    assertThrows(NotFoundException.class, () -> {
      horseService.getById(99999L);
    });
  }

  @Test
  void testServiceGetPedigree_Positive() throws Exception {
    HorseTreeDto pedigree = horseService.getPedigree(-7, 2);
    assertAll(() -> assertThat(pedigree).isNotNull(),
            () -> assertThat(pedigree.name()).containsIgnoringCase("Jenny"));
    if (pedigree.mother() != null) {
      assertThat(pedigree.mother().name()).containsIgnoringCase("Bella");
    }
    if (pedigree.father() != null) {
      assertThat(pedigree.father().name()).containsIgnoringCase("Sam");
    }
  }

  /**
   * getPedigree – Negative (invalid generation).
   * Calls getPedigree with an invalid generation (e.g., negative) and expects a ValidationException.
   */
  @Test
  void testServiceGetPedigree_Negative_InvalidGeneration() {
    assertThrows(ValidationException.class, () -> {
      horseService.getPedigree(-1, -1);
    });
  }

  /**
   * create – Positive.
   * Calls the create method of HorseService with a valid HorseCreateDto object and asserts that:
   * - A valid HorseDetailDto is returned.
   * - The returned horse details match the input.
   */
  @Test
  void testServiceCreateHorse_Positive() throws ValidationException, ConflictException {
    // Create a valid HorseCreateDto.
    HorseCreateDto createDto = new HorseCreateDto(
            "Jessy",                           // name
            "Description for new horse",       // description
            LocalDate.of(2020, 1, 1),            // dateOfBirth
            Sex.FEMALE,                        // sex
            null,                                // ownerId (must be a valid, positive owner id)
            null,                              // motherId (optional)
            null,                              // fatherId (optional)
            null,                              // image (optional)
            null                               // imageType (optional)
    );
    HorseDetailDto createdHorse = horseService.create(createDto);

    createdHorseId = createdHorse.id();

    assertAll(
            () -> assertThat(createdHorse).isNotNull(),
            () -> assertThat(createdHorse.name()).isEqualTo("Jessy"),
            () -> assertThat(createdHorse.description()).isEqualTo("Description for new horse"),
            () -> assertThat(createdHorse.dateOfBirth()).isEqualTo(LocalDate.of(2020, 1, 1)),
            () -> assertThat(createdHorse.sex()).isEqualTo(Sex.FEMALE));
  }

  /**
   * Cleanup method to delete any created horse after each test.
   */
  @AfterEach
  public void removeCreation() throws Exception {
    if (createdHorseId != null) {
      horseService.delete(createdHorseId);
      createdHorseId = null;
    }
  }
}
