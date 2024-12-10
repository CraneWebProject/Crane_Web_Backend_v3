package crane.userservice.exception;


import crane.userservice.common.exceptions.NotFoundException;

public class PasswordNotMatchException extends NotFoundException {
    public PasswordNotMatchException(String message) {
        super(message);
    }
}
