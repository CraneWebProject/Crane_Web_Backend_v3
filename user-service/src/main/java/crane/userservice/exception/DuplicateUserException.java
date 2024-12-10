package crane.userservice.exception;
import crane.userservice.common.exceptions.BadRequestException;

public class DuplicateUserException extends BadRequestException {
    public DuplicateUserException(String message) {
        super(message);
    }
}
