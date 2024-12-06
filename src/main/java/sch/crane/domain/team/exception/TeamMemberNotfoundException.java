package sch.crane.domain.team.exception;

import sch.crane.domain.common.exception.NotFoundException;

public class TeamMemberNotfoundException extends NotFoundException {
    public TeamMemberNotfoundException(String message) {
        super(message);
    }
}
