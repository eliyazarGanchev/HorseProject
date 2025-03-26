package at.ac.tuwien.sepr.assignment.individual.exception;

/**
 * Thrown to indicate that a deletion operation has failed.
 * This exception is intended to signal situations where an attempt to delete
 * an entity or resource was unsuccessful.
 */
public class FailedToDeleteException extends RuntimeException {

  /**
   * Constructs a new {@code FailedToDeleteException} with the specified detail message.
   *
   * @param message the detail message providing more information about the failure
   */
  public FailedToDeleteException(String message) {
    super(message);
  }

  /**
   * Constructs a new {@code FailedToDeleteException} with the specified detail message
   * and underlying cause.
   *
   * @param message the detail message providing more information about the deletion failure.
   * @param cause   the underlying reason (throwable) for the deletion failure.
   */
  public FailedToDeleteException(String message, Throwable cause) {
    super(message, cause);
  }
}
