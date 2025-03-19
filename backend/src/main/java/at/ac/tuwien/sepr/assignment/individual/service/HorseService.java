package at.ac.tuwien.sepr.assignment.individual.service;


import at.ac.tuwien.sepr.assignment.individual.dto.*;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import java.util.stream.Stream;

/**
 * Service for working with horses.
 */
public interface HorseService {
  /**
   * Lists all horses stored in the system.
   *
   * @return list of all stored horses
   */
  Stream<HorseListDto> allHorses();


  /**
   * Updates the horse with the ID given in {@code horse}
   * with the data given in {@code horse}
   * in the persistent data store.
   *
   * @param horse the horse to update
   * @return he updated horse
   * @throws NotFoundException if the horse with given ID does not exist in the persistent data store
   * @throws ValidationException if the update data given for the horse is in itself incorrect (description too long, no name, …)
   * @throws ConflictException if the update data given for the horse is in conflict the data currently in the system (owner does not exist, …)
   */
  HorseDetailDto update(HorseUpdateDto horse) throws NotFoundException, ValidationException, ConflictException;


  /**
   * Get the horse with given ID, with more detail information.
   * This includes the owner of the horse, and its parents.
   * The parents of the parents are not included.
   *
   * @param id the ID of the horse to get
   * @return the horse with ID {@code id}
   * @throws NotFoundException if the horse with the given ID does not exist in the persistent data store
   */
  HorseDetailDto getById(long id) throws NotFoundException;


  /**
   * Creates a new horse in the persistent data store.
   * The newly created horse will be stored with the provided details.
   *
   * @param horse the horse to create
   * @return the created horse with its assigned ID and details
   * @throws ValidationException if the provided horse data is invalid (e.g., missing name, incorrect birth date, etc.)
   * @throws ConflictException if the creation data is in conflict with existing records (e.g., owner does not exist)
   */
  HorseDetailDto create(HorseCreateDto horse) throws ValidationException, ConflictException;

  /**
   * Deletes a horse from the persistent data store.
   * Once deleted, the horse will no longer appear in the system,
   * and all its relationships will be removed without affecting other horses.
   * If the deleted horse was a parent of another horse, the link will be removed.
   *
   * @param id the unique identifier of the horse to delete
   * @return the details of the deleted horse
   * @throws NotFoundException if the horse with the given ID does not exist in the persistent data store
   * @throws ValidationException if the horse cannot be deleted due to business rule constraints
   */
  HorseDetailDto delete(long id) throws ValidationException, NotFoundException;

  /**
   * Searches for horses based on the provided search criteria.
   * <p>
   * The search criteria are specified in a {@link HorseSearchDto}. Each field in the DTO is optional;
   * if a field is null, that criterion will not be applied. The supported criteria are:
   * <ul>
   *   <li><b>Name:</b> A case-insensitive partial match against the horse's name.</li>
   *   <li><b>Description:</b> A case-insensitive partial match against the horse's description.</li>
   *   <li><b>Date of Birth:</b> Returns horses that have the specified date of birth.</li>
   *   <li><b>Sex:</b> An exact match against the horse's sex.</li>
   *   <li><b>Owner Name:</b> A case-insensitive partial match against the owner's full name (concatenation of first and last name).</li>
   * </ul>
   * If no criteria are provided, all horses will be returned.
   *
   * @param searchParameters the DTO containing the search filters
   * @return a stream of {@link HorseListDto} objects representing the horses that match the specified criteria
   */
  Stream<HorseListDto> search(HorseSearchDto searchParameters);
}
