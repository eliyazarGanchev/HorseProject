package at.ac.tuwien.sepr.assignment.individual.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import at.ac.tuwien.sepr.assignment.individual.dto.HorseCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

/**
 * Integration tests for the Horse REST API endpoint.
 */
@ActiveProfiles({"test", "datagen"}) // Enables "test" Spring profile during test execution
@SpringBootTest
@EnableWebMvc
@WebAppConfiguration
public class HorseEndpointTest {

  @Autowired
  private WebApplicationContext webAppContext;
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  // Field to hold the ID of the created horse for cleanup.
  private Long createdHorseId;

  /**
   * Sets up the MockMvc instance before each test.
   */
  @BeforeEach
  public void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
  }

  /**
   * Tests retrieving all horses from the endpoint.
   *
   * @throws Exception if the request fails
   */
  @Test
  public void gettingAllHorses() throws Exception {
    byte[] body = mockMvc
        .perform(MockMvcRequestBuilders
            .get("/horses")
            .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
        .andReturn().getResponse().getContentAsByteArray();

    List<HorseListDto> horseResult = objectMapper.readerFor(HorseListDto.class).<HorseListDto>readValues(body).readAll();

    assertThat(horseResult).isNotNull();
    assertThat(horseResult.size()).isGreaterThanOrEqualTo(1);
    assertThat(horseResult)
        .extracting(HorseListDto::id, HorseListDto::name)
        .contains(tuple(-1L, "Wendy"));
  }

  /**
   * GET Horse by ID – Positive.
   * Request a horse with a valid ID and assert that:
   * - HTTP 200 is returned.
   * - The JSON response contains the expected fields (id, name, dateOfBirth, etc.).
   */
  @Test
  public void testGetHorseById_Positive() throws Exception {
    String response = mockMvc.perform(get("/horses/{id}", -1)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

    HorseDetailDto horse = objectMapper.readValue(response, HorseDetailDto.class);
    assertAll(() -> assertThat(horse).isNotNull(),
            () -> assertThat(horse.id()).isEqualTo(-1L),
            () -> assertThat(horse.name()).containsIgnoringCase("Wendy"),
            () -> assertThat(horse.dateOfBirth()).isNotNull());

  }

  /**
   * GET Horse by ID – Negative.
   * Request a horse with a non-existent ID and assert that:
   * - HTTP 404 is returned.
   * - The error message contains appropriate text.
   */
  @Test
  public void testGetHorseById_Negative() throws Exception {
    mockMvc.perform(get("/horses/{id}", 99999)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andExpect(result ->
                    assertThat(result.getResolvedException().getMessage())
                            .containsIgnoringCase("No horse with ID 99999 found"));
  }

  /**
   * Search Horses – Positive.
   * Searches for horses by a valid partial name (e.g., "Wendy") and verifies that:
   * - HTTP 200 is returned.
   * - The response is an array containing at least one record matching the criteria.
   */
  @Test
  public void testSearchHorses_Positive() throws Exception {
    String response = mockMvc.perform(get("/horses")
                    .param("name", "Wendy")
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

    List<HorseListDto> horses = objectMapper.readValue(response, new TypeReference<List<HorseListDto>>() {});
    assertAll(() -> assertThat(horses).isNotEmpty(),
            () -> assertThat(horses)
            .extracting(HorseListDto::name)
            .anyMatch(name -> name.toLowerCase().contains("wendy")));

  }



  /**
   * Search Horses – Negative.
   * Searches for horses with a name that does not exist and verifies that:
   * - HTTP 200 is returned.
   * - The returned array is empty.
   */
  @Test
  public void testSearchHorses_Negative() throws Exception {
    String response = mockMvc.perform(get("/horses")
                    .param("name", "nonExistentHorseName")
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

    List<HorseListDto> horses = objectMapper.readValue(response, new TypeReference<List<HorseListDto>>() {});
    assertThat(horses).isEmpty();
  }

  /**
   * POST Horse – Positive.
   * Creates a new horse using a valid HorseCreateDto object (with name "Jessy")
   * and verifies that:
   * - HTTP 200 is returned.
   * - The JSON response contains the expected horse details.
   */
  @Test
  public void testCreateHorse_Positive_WithJessy() throws Exception {
    HorseCreateDto createDto = new HorseCreateDto(
            "Jessy",
            "Description for new horse",
            LocalDate.of(2020, 1, 1),
            Sex.FEMALE,
            null,
            null,
            null,
            null,
            null
    );

    String horseJson = objectMapper.writeValueAsString(createDto);
    MockMultipartFile horsePart = new MockMultipartFile(
            "horse",
            "horse.json",
            "application/json",
            horseJson.getBytes()
    );

    String response = mockMvc.perform(MockMvcRequestBuilders.multipart("/horses")
                    .file(horsePart)
                    .contentType(MediaType.MULTIPART_FORM_DATA)
                    .accept(MediaType.APPLICATION_JSON))
            .andReturn().getResponse().getContentAsString();

    HorseDetailDto createdHorse = objectMapper.readValue(response, HorseDetailDto.class);

    createdHorseId = createdHorse.id();

    assertAll(
            () -> assertThat(createdHorse).isNotNull(),
            () -> assertThat(createdHorse.name()).isEqualTo("Jessy"),
            () -> assertThat(createdHorse.description()).isEqualTo("Description for new horse"),
            () -> assertThat(createdHorse.dateOfBirth()).isEqualTo(LocalDate.of(2020, 1, 1)),
            () -> assertThat(createdHorse.sex()).isEqualTo(Sex.FEMALE)
    );
  }

  /**
   * Cleanup method to delete any created horse after each test.
   */
  @AfterEach
  public void removeCreation() throws Exception {
    if (createdHorseId != null) {
      mockMvc.perform(MockMvcRequestBuilders.delete("/horses/{id}", createdHorseId)
                      .accept(MediaType.APPLICATION_JSON))
              .andExpect(status().isOk());
      createdHorseId = null;
    }
  }



  /**
   * Tests that accessing a nonexistent URL returns a 404 status.
   *
   * @throws Exception if the request fails
   */
  @Test
  public void gettingNonexistentUrlReturns404() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders
            .get("/asdf123")
        ).andExpect(status().isNotFound());
  }
}
