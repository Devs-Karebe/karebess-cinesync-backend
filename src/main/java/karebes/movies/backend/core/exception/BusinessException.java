package karebes.movies.backend.core.exception;

/**
 * Business Exception
 * Thrown when a business rule is violated
 */
public class BusinessException extends RuntimeException {

    private final String userMessage;
    private final String errorCode;

    public BusinessException(
            String technicalMessage,
            String userMessage,
            String errorCode) {

        super(technicalMessage);
        this.userMessage = userMessage;
        this.errorCode = errorCode;
    }

    public BusinessException(
            String technicalMessage,
            String userMessage,
            String errorCode,
            Throwable cause) {

        super(technicalMessage, cause);
        this.userMessage = userMessage;
        this.errorCode = errorCode;
    }

    public String getUserMessage() {
        return userMessage;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
