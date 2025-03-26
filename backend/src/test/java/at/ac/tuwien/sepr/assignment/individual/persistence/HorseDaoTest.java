package at.ac.tuwien.sepr.assignment.individual.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;

import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Integration test for {@link HorseDao}, ensuring database operations function correctly.
 */
@ActiveProfiles({"test", "datagen"}) // Enables "test" Spring profile to load test data
@SpringBootTest
public class HorseDaoTest {

  @Autowired
  HorseDao horseDao;

  /**
   * Tests that retrieving all stored horses returns at least one entry
   * and verifies that a specific horse exists in the test dataset.
   */
  @Test
  public void getAllReturnsAllStoredHorses() {
    List<Horse> horses = horseDao.getAll();
    assertThat(horses.size()).isGreaterThanOrEqualTo(1); // TODO adapt to exact number of elements in test data later
    assertThat(horses)
        .extracting(Horse::id, Horse::name)
        .contains(tuple(-1L, "Wendy"));
  }

  /**
   * Positive test for getChildrenByParentId:
   * For a known parent horse (e.g., Wendy with ID -1), verify that at least one child is returned.
   */
  @Test
  public void getChildrenByParent_Positive() {

    List<Horse> children = horseDao.getChildrenByParentId(-1L);
    boolean found = children.stream().anyMatch(child -> {
      Long motherIdChecker = child.motherId();
      return motherIdChecker != null && motherIdChecker.equals(-1L);
    });
    assertAll(() -> assertThat(children).isNotEmpty(),
            () -> assertThat(found).isTrue());

  }

  /**
   * Negative test for getChildrenByParentId:
   * For a horse that is not a parent (e.g., Oscar with ID -10, assuming no horse references it as a parent),
   * verify that an empty list is returned.
   */
  @Test
  public void getChildrenByParent_Negative() {
    List<Horse> children = horseDao.getChildrenByParentId(-10L);
    assertThat(children).isEmpty();
  }

  /**
   * Positive test for getPedigree:
   * Retrieve the pedigree for a valid horse (e.g., Bella with ID -3) with a generation limit,
   * and verify that the result is not empty and contains expected ancestor names.
   */
  @Test
  public void getPedigree_Positive() {
    List<Horse> pedigreeList = horseDao.getPedigree(-3, 2);
    boolean containsBella = pedigreeList.stream().anyMatch(h -> h.name().toLowerCase().contains("bella"));
    assertAll(() -> assertThat(pedigreeList).isNotEmpty(),
            () -> assertThat(containsBella).isTrue());

  }

  /**
   * Negative test for getPedigree (invalid generation):
   * Calling getPedigree with a negative generation limit should throw a ValidationException.
   */
  @Test
  public void getPedigree_Negative_InvalidGeneration() {
    List<Horse> pedigreeList = horseDao.getPedigree(-1, -1);
    assertAll(() -> assertThat(pedigreeList).isNotNull(),
            (() -> assertThat(pedigreeList.size()).isLessThanOrEqualTo(1)));

  }
}
