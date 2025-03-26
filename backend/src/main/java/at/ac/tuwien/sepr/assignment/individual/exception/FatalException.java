package at.ac.tuwien.sepr.assignment.individual.exception;

/**
 * Exception used to signal unexpected and unrecoverable errors.
 */
public class FatalException extends RuntimeException {

  /**
   * Constructs a new {@code FatalException} with the specified detail message.
   *
   * @param message the detail message describing the fatal error.
   */
  public FatalException(String message) {
    super(message);
  }

  /**
   * Constructs a new {@code FatalException} with the specified underlying cause.
   *
   * @param cause the underlying cause (throwable) of the fatal error.
   */
  public FatalException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructs a new {@code FatalException} with the specified detail message and cause.
   *
   * @param message the detail message describing the fatal error.
   * @param cause   the underlying cause (throwable) of the fatal error.
   */
  public FatalException(String message, Throwable cause) {
    super(message, cause);
  }
}
