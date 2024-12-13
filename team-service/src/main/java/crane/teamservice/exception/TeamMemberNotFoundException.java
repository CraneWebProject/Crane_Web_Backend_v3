package crane.teamservice.exception;

import crane.teamservice.common.exceptions.NotFoundException;

public class TeamMemberNotFoundException extends NotFoundException {
    public TeamMemberNotFoundException(String message){super(message);}
}
