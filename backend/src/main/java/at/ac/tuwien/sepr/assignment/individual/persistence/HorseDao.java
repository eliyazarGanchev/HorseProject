package at.ac.tuwien.sepr.assignment.individual.persistence;


import at.ac.tuwien.sepr.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseTreeDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseUpdateDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import java.util.List;

/**
 * Data Access Object for horses.
 * Implements access functionality to the application's persistent data store regarding horses.
 */
public interface HorseDao {
  /**
   * Get all horses stored in the persistent data store.
   *
   * @return a list of all stored horses
   */
  List<Horse> getAll();


  /**
   * Update the horse with the ID given in {@code horse}
   * with the data given in {@code horse}
   * in the persistent data store.
   *
   * @param horse the horse to update
   * @return the updated horse
   * @throws NotFoundException if the Horse with the given ID does not exist in the persistent data store
   */
  Horse update(HorseUpdateDto horse) throws NotFoundException;


  /**
   * Get a horse by its ID from the persistent data store.
   *
   * @param id the ID of the horse to get
   * @return the horse
   * @throws NotFoundException if the Horse with the given ID does not exist in the persistent data store
   */
  Horse getById(long id) throws NotFoundException;


  /**
   * Creates a new horse in the persistent data store.
   * The newly created horse will be stored with the provided details.
   *
   * @param horse the horse data to create
   * @return the created horse entity with its assigned ID
   */
  Horse create(HorseCreateDto horse);

  /**
   * Deletes a horse from the persistent data store.
   * Once deleted, the horse will no longer appear in the system,
   * and all its relationships will be removed without affecting other horses.
   * If the deleted horse was a parent of another horse, the link will be removed.
   *
   * @param id the unique identifier of the horse to delete
   * @throws NotFoundException if the horse with the given ID does not exist in the persistent data store
   */
  void delete(long id) throws NotFoundException;

  /**
   * Searches for horses in the persistent data store based on the provided criteria.
   * The search criteria are encapsulated in a {@link HorseSearchDto} object, where each field is optional.
   * If a field is null, that criterion is not applied, and the search will not be restricted by it.
   * The supported search criteria are:
   * <ul>
   *   <li><b>Name:</b> A case-insensitive partial match against the horse's name.</li>
   *   <li><b>Description:</b> A case-insensitive partial match against the horse's description.</li>
   *   <li><b>Date of Birth:</b> Returns horses with a date of birth equal to the given date.</li>
   *   <li><b>Sex:</b> An exact match for the horse's sex.</li>
   *   <li><b>Owner Name:</b> A case-insensitive partial match against the owner's full name
   *       (combination of first and last name).</li>
   * </ul>
   * If no criteria are provided, the method returns all horses.
   *
   * @param searchParameters the DTO containing the search filters
   * @return a list of horses that match the provided search criteria
   */
  List<Horse> search(HorseSearchDto searchParameters);

  /**
   * Retrieves the pedigree (ancestry tree) for the horse with the specified ID.
   * The pedigree is constructed by recursively fetching the ancestors (mother and father)
   * of the specified horse. The recursion is limited by the given maximum number of generations.
   * If the parameter {@code maxGenerations} is {@code null}, no generation limit is applied
   * and the complete ancestry tree is returned.
   * The returned list contains all {@link Horse} entities that are part of the pedigree,
   * which can then be converted into a hierarchical structure (e.g., a {@link HorseTreeDto})
   * by the mapper.
   *
   * @param id the unique identifier of the horse for which to retrieve the pedigree
   * @param maxGenerations the maximum number of generations to include in the pedigree;
   *                       if {@code null}, the entire pedigree is returned
   * @return a list of {@link Horse} entities representing the ancestry of the specified horse
   */
  List<Horse> getPedigree(long id, Integer maxGenerations);

  /**
   * Retrieves all horses for which the given parent is registered as either the mother or the father.
   *
   * @param parentId the ID of the parent horse
   * @return a list of Horse entities that have this parent
   */
  List<Horse> getChildrenByParentId(Long parentId);
}
