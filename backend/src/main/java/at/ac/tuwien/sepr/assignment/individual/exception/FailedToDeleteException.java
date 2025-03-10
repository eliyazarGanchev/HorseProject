package at.ac.tuwien.sepr.assignment.individual.exception;

public class FailedToDeleteException extends RuntimeException {
    public FailedToDeleteException(String message) {
        super(message);
    }

    public FailedToDeleteException(String message, Throwable cause) {
        super(message, cause);
    }
}
