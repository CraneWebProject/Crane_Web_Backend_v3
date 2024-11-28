package sch.crane.domain.user.exception;


import sch.crane.domain.common.exception.NotFoundException;

public class PasswordNotMatchException extends NotFoundException {
    public PasswordNotMatchException(String message) {
        super(message);
    }
}
