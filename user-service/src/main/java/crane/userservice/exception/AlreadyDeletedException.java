package crane.userservice.exception;

import crane.userservice.common.exceptions.BadRequestException;

public class AlreadyDeletedException extends BadRequestException {
    public AlreadyDeletedException(String message) {
        super(message);
    }
}
