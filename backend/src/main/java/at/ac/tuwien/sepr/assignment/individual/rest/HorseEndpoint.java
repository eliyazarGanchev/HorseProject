package at.ac.tuwien.sepr.assignment.individual.rest;

import at.ac.tuwien.sepr.assignment.individual.dto.*;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.FailedToCreateException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.service.HorseService;
import java.lang.invoke.MethodHandles;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
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
    // TODO We have the request params in the DTO now, but don't do anything with them yet…

    return service.allHorses();
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

  @PostMapping
  public HorseDetailDto create(@RequestBody HorseCreateDto toCreate) {
    LOG.info("POST " + BASE_PATH);
    LOG.debug("request parameters: {}", toCreate);
    try{
      return service.create(toCreate);
    }catch (FailedToCreateException e){
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
