package sch.crane.domain.user.exception;

import sch.crane.domain.common.exception.BadRequestException;

public class UserExistsException extends BadRequestException {
    public UserExistsException(String message) {
        super(message);
    }
}
