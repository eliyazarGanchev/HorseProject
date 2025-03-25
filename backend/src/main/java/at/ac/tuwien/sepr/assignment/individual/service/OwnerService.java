package at.ac.tuwien.sepr.assignment.individual.service;

import at.ac.tuwien.sepr.assignment.individual.dto.OwnerCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.OwnerDto;
import at.ac.tuwien.sepr.assignment.individual.dto.OwnerSearchDto;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Stream;

/**
 * Service for working with owners.
 */
public interface OwnerService {
  /**
   * Fetch an owner from the persistent data store by its ID.
   *
   * @param id the ID of the owner to get
   * @return the owner with the given ID
   * @throws NotFoundException if no owner with the given ID exists in the persistent data store
   */
  OwnerDto getById(long id) throws NotFoundException;

  /**
   * Fetch all owners referenced by the IDs in {@code ids}
   *
   * @param ids the IDs of the owners, that should be fetched
   * @return a map that contains the requested owners with their IDs as key
   * @throws NotFoundException if any of the requested owners is not found
   */
  Map<Long, OwnerDto> getAllById(Collection<Long> ids) throws NotFoundException;

  /**
   * Search for owners matching the criteria in {@code searchParameters}.
   * An owner is considered matched, if its name contains {@code searchParameters.name} as a substring.
   * The returned stream of owners never contains more than {@code searchParameters.maxAmount} elements,
   * even if there would be more matches in the persistent data store.
   *
   * @param searchParameters object containing the search parameters to match
   * @return a stream containing owners matching the criteria in {@code searchParameters}
   */
  Stream<OwnerDto> search(OwnerSearchDto searchParameters);

  /**
   * Creates a new owner in the persistent data store.
   * The newly created owner will be stored with the provided details.
   * The owner is validated before being persisted.
   *
   * @param toCreate the DTO containing the details for the owner to be created
   * @return the created owner, including its assigned ID and other details
   * @throws ValidationException if the provided owner data is invalid (e.g., missing required fields)
   */
  OwnerDto create(OwnerCreateDto toCreate) throws ValidationException;
}
