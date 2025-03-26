package at.ac.tuwien.sepr.assignment.individual.rest;

import java.util.List;

/**
 * Represents a Data Transfer Object (DTO) for encapsulating validation errors
 * returned in REST API responses.
 * Contains a summary message that describes the overall validation error, and a detailed list
 * of specific validation errors encountered during request processing.
 *
 * @param message a brief summary describing the overall validation failure.
 * @param errors  a list of individual error messages detailing each specific validation issue.
 */
public record ValidationErrorRestDto(
    String message,
    List<String> errors
) {
}
