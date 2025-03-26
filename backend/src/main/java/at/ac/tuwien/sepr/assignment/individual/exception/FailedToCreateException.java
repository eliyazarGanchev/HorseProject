package at.ac.tuwien.sepr.assignment.individual.exception;

/**
 * Thrown to indicate that a creation operation has failed.
 * This exception can be used to signal that an attempt to create
 * a particular resource or entity was not successful.
 */
public class FailedToCreateException extends RuntimeException {

  /**
  * Constructs a new {@code FailedToCreateException} with the specified detail message.
  *
  * @param message the detail message providing more information about the cause of this exception
  */
  public FailedToCreateException(String message) {
    super(message);
  }

  /**
   * Constructs a new {@code FailedToCreateException} with the specified detail message
   * and underlying cause.
   *
   * @param message the detail message providing additional information about the cause of this exception.
   * @param cause   the underlying reason (throwable) for the creation failure.
   */
  public FailedToCreateException(String message, Throwable cause) {
    super(message, cause);
  }
}
