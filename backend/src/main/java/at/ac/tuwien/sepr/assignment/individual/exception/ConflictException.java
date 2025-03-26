package at.ac.tuwien.sepr.assignment.individual.exception;

import java.util.List;

/**
 * Exception that signals, that data,
 * that came from outside the backend, conflicts with the current state of the system.
 * The data violates some constraint on relationships
 * (rather than an invariant).
 * Contains a list of all conflict checks that failed when validating the piece of data in question.
 */
public class ConflictException extends ErrorListException {

  /**
   * Constructs a new {@code ConflictException} with a general summary and a detailed list
   * of conflict errors.
   *
   * @param messageSummary a brief summary of the conflict situation.
   * @param errors         a list of specific conflict errors identified during validation.
   */
  public ConflictException(String messageSummary, List<String> errors) {
    super("Conflicts", messageSummary, errors);
  }
}
