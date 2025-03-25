package at.ac.tuwien.sepr.assignment.individual.mapper;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseTreeDto;
import at.ac.tuwien.sepr.assignment.individual.dto.OwnerDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.persistence.HorseDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Mapper class responsible for converting {@link Horse} entities into various DTOs.
 */
@Component
public class HorseMapper {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());


  private final HorseDao horseDao;

  public HorseMapper(HorseDao horseDao) {
    this.horseDao = horseDao;
  }

  /**
   * Converts a {@link Horse} entity into a {@link HorseListDto}.
   * The given map of owners must contain the owner referenced by the horse.
   *
   * @param horse  the horse entity to convert
   * @param owners a map of horse owners by their ID
   * @return the converted {@link HorseListDto}
   */
  public HorseListDto entityToListDto(Horse horse, Map<Long, OwnerDto> owners) {
    LOG.trace("entityToDto({})", horse);
    if (horse == null) {
      return null;
    }

    return new HorseListDto(
        horse.id(),
        horse.name(),
        horse.description(),
        horse.dateOfBirth(),
        horse.sex(),
        getOwner(horse, owners)
    );
  }

  /**
   * Converts a {@link Horse} entity into a {@link HorseDetailDto}.
   * The given maps must contain the owners and parents referenced by the horse.
   *
   * @param horse   the horse entity to convert
   * @param owners  a map of horse owners by their ID
   * @return the converted {@link HorseDetailDto}
   */
  public HorseDetailDto entityToDetailDto(
      Horse horse,
      Map<Long, OwnerDto> owners) {
    LOG.trace("entityToDto({})", horse);
    if (horse == null) {
      return null;
    }

    return new HorseDetailDto(
        horse.id(),
        horse.name(),
        horse.description(),
        horse.dateOfBirth(),
        horse.sex(),
        getOwner(horse, owners),
        horse.motherId() == null? null: getParent(horse.motherId()),
        horse.fatherId() == null? null: getParent(horse.fatherId()),
        horse.image(),
        horse.imageType()
    );
  }

  private HorseDetailDto getParent(Long parentId) {
    if (parentId == null) {
      return null;
    }
    try {
      Horse parent = horseDao.getById(parentId);
      return new HorseDetailDto(
              parent.id(),
              parent.name(),
              null,
              parent.dateOfBirth(),
              parent.sex(),
              null,
              null,
              null,
              null,
              null
      );
    } catch (NotFoundException e) {
      throw new FatalException("Parent horse with ID %d not found".formatted(parentId), e);
    }
  }

  private OwnerDto getOwner(Horse horse, Map<Long, OwnerDto> owners) {
    OwnerDto owner = null;
    var ownerId = horse.ownerId();
    if (ownerId != null) {
      if (!owners.containsKey(ownerId)) {
        throw new FatalException("Given owner map does not contain owner of this Horse (%d)".formatted(horse.id()));
      }
      owner = owners.get(ownerId);
    }
    return owner;
  }

  /**
   * Converts a flat list of {@link Horse} entities (representing the pedigree)
   * into a hierarchical {@link HorseTreeDto} for the horse with the given id.
   * The list is expected to contain the horse with the given id and all its ancestors.
   * The returned tree contains for each horse only the id, name, date of birth, sex, and its parent subtrees.
   *
   * @param id the ID of the root horse whose pedigree is to be built
   * @param results the list of Horse entities representing the pedigree (ancestors)
   * @return a hierarchical {@link HorseTreeDto} representing the horse’s pedigree
   * @throws FatalException if the root horse with the given id is not found in the list
   */
  public HorseTreeDto entityToTreeDto(long id, List<Horse> results) {
    LOG.trace("entityToDto({}, {})", id, results);
    Map<Long, Horse> horseMap = results.stream()
            .collect(Collectors.toMap(Horse::id, Function.identity(),
                    (address1, address2) -> address1));
    LOG.debug("Horse whose tree is being built: {}", horseMap.get(id));
    return convertToTreeDto(horseMap.get(id), horseMap);
  }

  /**
   * Recursively converts a Horse entity and its parents (if available in the map)
   * into a HorseTreeDto.
   *
   * @param horse the horse entity to convert
   * @param horseMap a map from horse id to Horse entity (all ancestors)
   * @return the corresponding {@link HorseTreeDto}
   */
  private HorseTreeDto convertToTreeDto(Horse horse, Map<Long, Horse> horseMap) {
    HorseTreeDto motherDto = null;
    if (horse.motherId() != null) {
      Horse mother = horseMap.get(horse.motherId());
      if (mother != null) {
        motherDto = convertToTreeDto(mother, horseMap);
      }
    }
    HorseTreeDto fatherDto = null;
    if (horse.fatherId() != null) {
      Horse father = horseMap.get(horse.fatherId());
      if (father != null) {
        fatherDto = convertToTreeDto(father, horseMap);
      }
    }

    return new HorseTreeDto(
            horse.id(),
            horse.name(),
            horse.dateOfBirth(),
            horse.sex(),
            motherDto,
            fatherDto
    );
  }
}
