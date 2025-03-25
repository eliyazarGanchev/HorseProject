package at.ac.tuwien.sepr.assignment.individual.persistence.impl;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseSearchDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseUpdateDto;
import at.ac.tuwien.sepr.assignment.individual.entity.Horse;
import at.ac.tuwien.sepr.assignment.individual.exception.FatalException;
import at.ac.tuwien.sepr.assignment.individual.exception.NotFoundException;
import at.ac.tuwien.sepr.assignment.individual.persistence.HorseDao;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import java.lang.invoke.MethodHandles;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

/**
 * JDBC implementation of {@link HorseDao} for interacting with the database.
 */
@Repository
public class HorseJdbcDao implements HorseDao {
  private static final Logger LOG = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  private static final String TABLE_NAME = "horse";

  private static final String SQL_SELECT_ALL =
      "SELECT * FROM " + TABLE_NAME;

  private static final String SQL_SELECT_BY_ID =
      "SELECT * FROM " + TABLE_NAME
          + " WHERE ID = :id";

  private static final String SQL_UPDATE =
      "UPDATE " + TABLE_NAME
          + """
              SET name = :name,
                  description = :description,
                  date_of_birth = :date_of_birth,
                  sex = :sex,
                  owner_id = :owner_id,
                  mother_id = :mother_id,
                  father_id = :father_id,
                  image = :image,
                  image_type = :image_type
              WHERE id = :id
          """;

  private static final String SQL_INSERT =
          "INSERT INTO " + TABLE_NAME + " (name, description, date_of_birth, sex, owner_id, mother_id, father_id, image, image_type) " +
                  "VALUES (:name, :description, :date_of_birth, :sex, :owner_id, :mother_id, :father_id, :image, :image_type)";

  private static final String SQL_DELETE =
          "DELETE FROM " + TABLE_NAME + " WHERE id = :id";

  private static final String SQL_SEARCH =
          "SELECT h.*" + " FROM " + TABLE_NAME + " h " +
                  "LEFT JOIN owner o ON h.owner_id = o.id " +
                  "WHERE (COALESCE(:name, '') = '' OR UPPER(h.name) LIKE UPPER(:name) || '%') " +
                  "AND (COALESCE(:description, '') = '' OR UPPER(h.description) LIKE '%' || UPPER(:description) || '%') " +
                  "AND (:date_of_birth IS NULL OR h.date_of_birth <= :date_of_birth) " +
                  "AND (:sex IS NULL OR h.sex = :sex) " +
                  "AND (COALESCE(:ownerName, '') = '' OR UPPER(o.first_name || ' ' || o.last_name) LIKE UPPER(:ownerName) || '%')";

  private static final String SQL_GET_PEDIGREE =
          "WITH RECURSIVE pedigree (id, name, date_of_birth, sex, mother_id, father_id, generation) AS ( " +
                  "SELECT id, name, date_of_birth, sex, mother_id, father_id, 0 AS generation " +
                  "FROM " + TABLE_NAME + " " +
                  "WHERE id = :id " +
                  "UNION ALL " +
                  "SELECT h.id, h.name, h.date_of_birth, h.sex, h.mother_id, h.father_id, p.generation + 1 " +
                  "FROM " + TABLE_NAME + " h " +
                  "INNER JOIN pedigree p ON h.id = p.mother_id OR h.id = p.father_id " +
                  "WHERE (:maxGenerations IS NULL OR p.generation < :maxGenerations) " +
                  ") " +
                  "SELECT id, name, date_of_birth, sex, mother_id, father_id " +
                  "FROM pedigree";

  private final JdbcClient jdbcClient;

  @Autowired
  public HorseJdbcDao(JdbcClient jdbcClient) {
    this.jdbcClient = jdbcClient;
  }

  @Override
  public List<Horse> getAll() {
    LOG.trace("getAll()");
    return jdbcClient
        .sql(SQL_SELECT_ALL)
        .query(this::mapRow)
        .list();
  }

  @Override
  public Horse getById(long id) throws NotFoundException {
    LOG.trace("getById({})", id);
    List<Horse> horses = jdbcClient
        .sql(SQL_SELECT_BY_ID)
        .param("id", id)
        .query(this::mapRow)
        .list();

    if (horses.isEmpty()) {
      throw new NotFoundException("No horse with ID %d found".formatted(id));
    }
    if (horses.size() > 1) {
      // This should never happen!!
      throw new FatalException("Too many horses with ID %d found".formatted(id));
    }

    return horses.getFirst();
  }

  @Override
  public Horse create(HorseCreateDto horse) {
    LOG.trace("create({})", horse);
    KeyHolder keyHolder = new GeneratedKeyHolder();
    int created = jdbcClient
            .sql(SQL_INSERT)
            .param("name", horse.name())
            .param("description", horse.description())
            .param("date_of_birth", horse.dateOfBirth())
            .param("sex", horse.sex().toString())
            .param("owner_id", horse.ownerId())
            .param("mother_id",horse.motherId())
            .param("father_id", horse.fatherId())
            .param("image", horse.image())
            .param("image_type", horse.imageType())
            .update(keyHolder);

    if (created == 0) {
      throw new FatalException("Failed to create horse");
    }

    Long createdId = Objects.requireNonNull(keyHolder.getKey()).longValue();

    return new Horse(
            createdId,
            horse.name(),
            horse.description(),
            horse.dateOfBirth(),
            horse.sex(),
            horse.ownerId(),
            horse.motherId(),
            horse.fatherId(),
            horse.image(),
            horse.imageType()
    );
  }

  @Override
  public void delete(long id) throws NotFoundException {
    LOG.trace("delete({})", id);
    int deleted = jdbcClient.sql(SQL_DELETE)
            .param("id", id)
            .update();

    if (deleted == 0) {
      throw new NotFoundException("Failed to delete horse with ID " + id);
    }
  }

  @Override
  public List<Horse> search(HorseSearchDto searchParameters) {
    LOG.trace("search({})", searchParameters);

    return jdbcClient.sql(SQL_SEARCH)
            .param("name", searchParameters.name())
            .param("description", searchParameters.description())
            .param("date_of_birth", searchParameters.date_of_birth())
            .param("sex", searchParameters.sex() != null ? searchParameters.sex().toString() : null)
            .param("ownerName", searchParameters.ownerName())
            .query(this::mapRow)
            .list();
  }

  @Override
  public List<Horse> getPedigree(long id, Integer maxGenerations) {
    LOG.trace("getPedigree({}, {})", id, maxGenerations);
    return jdbcClient.sql(SQL_GET_PEDIGREE)
            .param("id", id)
            .param("maxGenerations", maxGenerations)
            .query(this::mapRowPedigree)
            .list();
  }


  @Override
  public Horse update(HorseUpdateDto horse) throws NotFoundException {
    LOG.trace("update({})", horse);
    int updated = jdbcClient
        .sql(SQL_UPDATE)
        .param("id", horse.id())
        .param("name", horse.name())
        .param("description", horse.description())
        .param("date_of_birth", horse.dateOfBirth())
        .param("sex", horse.sex().toString())
        .param("owner_id", horse.ownerId())
        .param("mother_id", horse.motherId())
        .param("father_id", horse.fatherId())
        .param("image", horse.image())
        .param("image_type", horse.imageType())
        .update();

    if (updated == 0) {
      throw new NotFoundException(
          "Could not update horse with ID " + horse.id() + ", because it does not exist"
      );
    }

    return new Horse(
        horse.id(),
        horse.name(),
        horse.description(),
        horse.dateOfBirth(),
        horse.sex(),
        horse.ownerId(),
        horse.motherId(),
        horse.fatherId(),
        horse.image(),
        horse.imageType());
  }


  private Horse mapRow(ResultSet result, int rownum) throws SQLException {
    return new Horse(
        result.getLong("id"),
        result.getString("name"),
        result.getString("description"),
        result.getDate("date_of_birth").toLocalDate(),
        Sex.valueOf(result.getString("sex")),
        result.getObject("owner_id", Long.class),
        result.getObject("mother_id", Long.class),
        result.getObject("father_id", Long.class),
        result.getBytes("image"),
        result.getString("image_type"));
  }

  private Horse mapRowPedigree(ResultSet result, int rownum) throws SQLException {
    return new Horse(
            result.getLong("id"),
            result.getString("name"),
            null,
            result.getDate("date_of_birth").toLocalDate(),
            Sex.valueOf(result.getString("sex")),
            null,
            result.getObject("mother_id", Long.class),
            result.getObject("father_id", Long.class),
            null,
            null
    );
  }

}
