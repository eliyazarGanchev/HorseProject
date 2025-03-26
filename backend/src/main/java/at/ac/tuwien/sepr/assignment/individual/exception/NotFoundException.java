package at.ac.tuwien.sepr.assignment.individual.exception;

/**
 * Exception that signals, that whatever resource,
 * that has been tried to access,
 * was not found.
 */
public class NotFoundException extends Exception {

  /**
   * Constructs a new {@code NotFoundException} with the specified detail message.
   *
   * @param message the detail message describing the missing resource.
   */
  public NotFoundException(String message) {
    super(message);
  }

  /**
   * Constructs a new {@code NotFoundException} with the specified underlying cause.
   *
   * @param cause the underlying cause (throwable) leading to this exception.
   */
  public NotFoundException(Throwable cause) {
    super(cause);
  }

  /**
   * Constructs a new {@code NotFoundException} with the specified detail message and cause.
   *
   * @param message the detail message describing the missing resource.
   * @param cause   the underlying cause (throwable) leading to this exception.
   */
  public NotFoundException(String message, Throwable cause) {
    super(message, cause);
  }
}
