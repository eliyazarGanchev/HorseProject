package at.ac.tuwien.sepr.assignment.individual;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseTreeDto;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.service.HorseService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles({"test", "datagen"}) // enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
class SeprIndividualAssignmentApplicationTests {

  @Autowired
  private HorseService horseService;

  @Test
  void contextLoads() {
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
    assertThat(horse).isNotNull();
    assertThat(horse.id()).isEqualTo(-4L);
    assertThat(horse.name()).containsIgnoringCase("Daisy");
    assertThat(horse.dateOfBirth()).isNotNull();
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
    assertThat(pedigree).isNotNull();
    assertThat(pedigree.name()).containsIgnoringCase("Jenny");
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

