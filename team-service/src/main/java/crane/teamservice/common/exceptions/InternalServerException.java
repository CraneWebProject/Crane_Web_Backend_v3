package crane.teamservice.common.exceptions;

public class InternalServerException extends RuntimeException{
    public InternalServerException(String message) {
        super(message);
    }
}
