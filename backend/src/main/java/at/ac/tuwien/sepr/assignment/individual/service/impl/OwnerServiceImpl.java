package at.ac.tuwien.sepr.assignment.individual.service.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.OwnerCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.OwnerDto;
import at.ac.tuwien.sepr.assignment.individual.dto.OwnerSearchDto;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.mapper.OwnerMapper;
import at.ac.tuwien.sepr.assignment.individual.persistence.OwnerDao;
import at.ac.tuwien.sepr.assignment.individual.service.OwnerService;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service implementation for managing owner-related operations.
 */
@Service
public class OwnerServiceImpl implements OwnerService {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private final OwnerDao dao;
  private final OwnerMapper mapper;
  private final OwnerValidator validator;

  /**
   * Constructs a new {@code OwnerServiceImpl} with required dependencies.
   *
   * @param dao the data access object responsible for owner-related database operations.
   * @param mapper the mapper that converts between owner entities and data transfer objects.
   * @param validator the validator used to validate owner-related input data.
   */
  public OwnerServiceImpl(
          OwnerDao dao,
          OwnerMapper mapper, OwnerValidator validator) {
    this.dao = dao;
    this.mapper = mapper;
    this.validator = validator;
  }

  @Override
  public OwnerDto getById(long id) throws NotFoundException {
    LOG.trace("getById({})", id);
    return mapper.entityToDto(dao.getById(id));
  }

  @Override
  public Map<Long, OwnerDto> getAllById(Collection<Long> ids) throws NotFoundException {
    LOG.trace("getAllById({})", ids);
    Map<Long, OwnerDto> owners =
        dao.getAllById(ids).stream()
            .map(mapper::entityToDto)
            .collect(Collectors.toUnmodifiableMap(OwnerDto::id, Function.identity()));
    for (final var id : ids) {
      if (!owners.containsKey(id)) {
        throw new NotFoundException("Owner with ID %d not found".formatted(id));
      }
    }
    return owners;
  }

  @Override
  public Stream<OwnerDto> search(OwnerSearchDto searchParameters) {
    LOG.trace("search({})", searchParameters);
    return dao.search(searchParameters).stream()
        .map(mapper::entityToDto);
  }

  @Override
  public OwnerDto create(OwnerCreateDto owner) throws ValidationException {
    LOG.trace("create({})", owner);
    validator.validateForCreate(owner);
    var createdOwner = dao.create(owner);
    return mapper.entityToDto(createdOwner);
  }
}
