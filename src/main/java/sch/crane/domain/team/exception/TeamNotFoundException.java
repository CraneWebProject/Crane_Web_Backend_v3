package sch.crane.domain.team.exception;

import sch.crane.domain.common.exception.NotFoundException;

public class TeamNotFoundException extends NotFoundException {
    public TeamNotFoundException(String message) {
        super(message);
    }
}
