package at.ac.tuwien.sepr.assignment.individual.service.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.*;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.exception.ConflictException;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.exception.ValidationException;
import at.ac.tuwien.sepr.assignment.individual.mapper.HorseMapper;
import at.ac.tuwien.sepr.assignment.individual.persistence.HorseDao;
import at.ac.tuwien.sepr.assignment.individual.service.HorseService;
import at.ac.tuwien.sepr.assignment.individual.service.OwnerService;
import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation of {@link HorseService} for handling image storage and retrieval.
 */
@Service
public class HorseServiceImpl implements HorseService {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  private final HorseDao dao;
  private final HorseMapper mapper;
  private final HorseValidator validator;
  private final OwnerService ownerService;


  @Autowired
  public HorseServiceImpl(HorseDao dao,
                          HorseMapper mapper,
                          HorseValidator validator,
                          OwnerService ownerService) {
    this.dao = dao;
    this.mapper = mapper;
    this.validator = validator;
    this.ownerService = ownerService;
  }

  @Override
  public Stream<HorseListDto> allHorses() {
    LOG.trace("allHorses()");
    var horses = dao.getAll();
    var ownerIds = horses.stream()
        .map(Horse::ownerId)
        .filter(Objects::nonNull)
        .collect(Collectors.toUnmodifiableSet());
    Map<Long, OwnerDto> ownerMap;
    try {
      ownerMap = ownerService.getAllById(ownerIds);
    } catch (NotFoundException e) {
      throw new FatalException("Horse, that is already persisted, refers to non-existing owner", e);
    }
    return horses.stream()
        .map(horse -> mapper.entityToListDto(horse, ownerMap));
  }


  @Override
  public HorseDetailDto update(HorseUpdateDto horse) throws NotFoundException, ValidationException, ConflictException {
    LOG.trace("update({})", horse);
    validator.validateForUpdate(horse);

    var updatedHorse = dao.update(horse);
    return mapper.entityToDetailDto(
        updatedHorse,
        ownerMapForSingleId(updatedHorse.ownerId()));
  }


  @Override
  public HorseDetailDto getById(long id) throws NotFoundException {
    LOG.trace("details({})", id);
    Horse horse = dao.getById(id);
    return mapper.entityToDetailDto(
        horse,
        ownerMapForSingleId(horse.ownerId()));
  }

  @Override
  public HorseDetailDto create(HorseCreateDto horse) throws ValidationException, ConflictException {
    LOG.trace("create({})", horse);
    validator.validateForCreate(horse);
    var createdHorse = dao.create(horse);
    return mapper.entityToDetailDto(createdHorse,ownerMapForSingleId(createdHorse.ownerId()));
  }

  @Override
  public HorseDetailDto delete(long id) throws ValidationException, NotFoundException {
    LOG.trace("delete({})", id);
    validator.validateForDelete(id);
    Horse horseToDeletion = dao.getById(id);
    dao.delete(id);
    return mapper.entityToDetailDto(horseToDeletion,ownerMapForSingleId(horseToDeletion.ownerId()));
  }

  @Override
  public Stream<HorseListDto> search(HorseSearchDto searchParameters) {
    LOG.trace("search({})", searchParameters);
    return dao.search(searchParameters).stream()
            .map(horse -> mapper.entityToListDto(horse, ownerMapForSingleId(horse.ownerId())));
  }

  @Override
  public HorseTreeDto getPedigree(long id, Integer maxGenerations) throws NotFoundException, ValidationException {
    LOG.trace("getPedigree({}, {})", id, maxGenerations);
    validator.validatePedigreeGenerations(maxGenerations);
    List<Horse> results = dao.getPedigree(id, maxGenerations);
    return mapper.entityToTreeDto(id, results);
  }


  private Map<Long, OwnerDto> ownerMapForSingleId(Long ownerId) {
    try {
      return ownerId == null
          ? null
          : Collections.singletonMap(ownerId, ownerService.getById(ownerId));
    } catch (NotFoundException e) {
      throw new FatalException("Owner %d referenced by horse not found".formatted(ownerId));
    }
  }

}
