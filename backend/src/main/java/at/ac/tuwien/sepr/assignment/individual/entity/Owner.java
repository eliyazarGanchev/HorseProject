package at.ac.tuwien.sepr.assignment.individual.entity;

/**
 * Represents an owner entity in the persistent data store.
 * This record encapsulates essential owner attributes, including identifying details
 * and optional descriptive information.
 *
 * @param id            the unique identifier of the owner.
 * @param firstName     the first name of the owner.
 * @param lastName      the last name of the owner.
 * @param description   an optional description or additional information about the owner.
 */
public record Owner(
    Long id,
    String firstName,
    String lastName,
    String description
) {
}
