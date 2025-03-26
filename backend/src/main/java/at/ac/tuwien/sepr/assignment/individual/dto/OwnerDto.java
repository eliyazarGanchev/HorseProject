package at.ac.tuwien.sepr.assignment.individual.dto;

/**
 * Represents a Data Transfer Object (DTO) for owner details.
 * This record encapsulates the essential information about an owner.
 *
 * @param id            the unique identifier of the owner.
 * @param firstName     the first name of the owner.
 * @param lastName      the last name of the owner.
 * @param description   a brief description or additional information about the owner (optional, may be empty or null).
 */
public record OwnerDto(
    long id,
    String firstName,
    String lastName,
    String description
) {
}
