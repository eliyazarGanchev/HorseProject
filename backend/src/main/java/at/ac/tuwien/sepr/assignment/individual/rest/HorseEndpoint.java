package at.ac.tuwien.sepr.assignment.individual.rest;


import at.ac.tuwien.sepr.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseUpdateRestDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseTreeDto;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToCreateException;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToDeleteException;
import at.ac.tuwien.sepr.assignment.individual.service.HorseService;
import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

/**
 * REST controller for managing horse-related operations.
 * Provides endpoints for searching, retrieving, creating, updating, and deleting horses,
 * as well as fetching their family tree.
 */
@RestController
@RequestMapping(path = HorseEndpoint.BASE_PATH)
public class HorseEndpoint {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  static final String BASE_PATH = "/horses";

  private final HorseService service;

  /**
   * Constructs a new {@code HorseEndpoint} instance with the specified service dependency.
   *
   * @param service the {@link HorseService} used to handle business logic related to horse entities.
   */
  @Autowired
  public HorseEndpoint(HorseService service) {
    this.service = service;
  }

  /**
   * Searches for horses based on the given search parameters.
   *
   * @param searchParameters the parameters to filter the horse search
   * @return a stream of {@link HorseListDto} matching the search criteria
   */
  @GetMapping
  public Stream<HorseListDto> searchHorses(HorseSearchDto searchParameters) {
    LOG.info("GET " + BASE_PATH);
    LOG.debug("request parameters: {}", searchParameters);
    return service.search(searchParameters);
  }

  /**
   * Retrieves the details of a horse by its ID.
   *
   * @param id the unique identifier of the horse
   * @return the detailed information of the requested horse
   * @throws ResponseStatusException if the horse is not found
   */
  @GetMapping("{id}")
  public HorseDetailDto getById(@PathVariable("id") long id) {
    LOG.info("GET " + BASE_PATH + "/{}", id);
    try {
      return service.getById(id);
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Horse to get details of not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }


  /**
   * Updates the details of an existing horse, including an optional image file.
   *
   * @param id        the ID of the horse to update
   * @param toUpdate  the updated horse data
   * @return the updated horse details
   * @throws ValidationException     if validation fails
   * @throws ConflictException       if a conflict occurs while updating
   * @throws ResponseStatusException if the horse is not found
   */
  @PutMapping(path = "{id}")
  public HorseDetailDto update(
      @PathVariable("id") long id,
      @RequestBody HorseUpdateRestDto toUpdate)
      throws ValidationException, ConflictException {
    LOG.info("PUT " + BASE_PATH + "/{}", toUpdate);
    LOG.debug("Body of request:\n{}", toUpdate);
    try {
      return service.update(toUpdate.toUpdateDtoWithId(id));
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Horse to update not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }


  /**
   * Creates a new horse in the system.
   *
   * @param toCreate the details of the horse to be created
   * @return the detailed information of the newly created horse
   * @throws FailedToCreateException if an unexpected error occurs during creation
   * @throws ResponseStatusException if the creation process fails due to validation, conflict, or an internal error
   */
  @PostMapping
  public HorseDetailDto create(@RequestBody HorseCreateDto toCreate) {
    LOG.info("POST " + BASE_PATH);
    LOG.debug("request parameters: {}", toCreate);
    try {
      return service.create(toCreate);
    } catch (FailedToCreateException e) {
      HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
      logClientError(status, "Horse creation failed", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (ValidationException e) {
      HttpStatus status = HttpStatus.BAD_REQUEST;
      logClientError(status, "Horse creation failed due to missing or incorrect data", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (ConflictException e) {
      HttpStatus status = HttpStatus.CONFLICT;
      logClientError(status, "Horse creation failed due to existing record conflict", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  /**
   * Deletes a horse from the system.
   *
   * @param id the unique identifier of the horse to delete
   * @return the detailed information of the deleted horse
   * @throws FailedToDeleteException if an unexpected error occurs during deletion
   * @throws ResponseStatusException if the deletion process fails due to validation, missing records, or internal errors
   */
  @DeleteMapping(path = "/{id}")
  public HorseDetailDto delete(@PathVariable("id") long id) {
    LOG.info("DELETE " + BASE_PATH + "/{}", id);
    LOG.debug("request parameters: {}", id);
    try {
      return service.delete(id);
    } catch (FailedToDeleteException e) {
      HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
      logClientError(status, "Horse to delete not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (ValidationException e) {
      HttpStatus status = HttpStatus.BAD_REQUEST;
      logClientError(status, "Horse to delete failed", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Horse to delete not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }

  /**
   * Returns the pedigree (family tree of ancestors) for the horse with the given ID.
   *
   * @param id the unique identifier of the horse
   * @param maxGenerations (optional) how many generations of ancestors to fetch.
   * @return a HorseTreeDto that includes mother/father up to the specified depth
   */
  @GetMapping("/{id}/pedigree")
  public HorseTreeDto getPedigree(
          @PathVariable("id") long id,
          @RequestParam(value = "maxGenerations", required = false) Integer maxGenerations
  ) {
    LOG.info("GET " +  BASE_PATH + "/{}" + "/{}", id, maxGenerations);
    try {
      return service.getPedigree(id, maxGenerations);
    } catch (NotFoundException e) {
      HttpStatus status = HttpStatus.NOT_FOUND;
      logClientError(status, "Pedigree fetch failed: Horse not found", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    } catch (ValidationException e) {
      HttpStatus status = HttpStatus.BAD_REQUEST;
      logClientError(status, "Pedigree fetch failed", e);
      throw new ResponseStatusException(status, e.getMessage(), e);
    }
  }


  /**
   * Logs client-side errors with relevant details.
   *
   * @param status  the HTTP status code of the error
   * @param message a brief message describing the error
   * @param e       the exception that occurred
   */
  private void logClientError(HttpStatus status, String message, Exception e) {
    LOG.warn("{} {}: {}: {}", status.value(), message, e.getClass().getSimpleName(), e.getMessage());
  }

}
