package at.ac.tuwien.sepr.assignment.individual.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseTreeDto;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import java.util.List;
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

  /**
   * Tests whether retrieving all stored horses returns the expected number and specific entries.
   */
  @Test
  public void getAllReturnsAllStoredHorses() {
    List<HorseListDto> horses = horseService.allHorses()
        .toList();

    assertThat(horses.size()).isGreaterThanOrEqualTo(1); // TODO: Adapt to exact number of test data entries

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
}
