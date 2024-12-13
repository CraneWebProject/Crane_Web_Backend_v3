package crane.teamservice.exception;

import crane.teamservice.common.exceptions.NotFoundException;

public class TeamNotFoundException extends NotFoundException {
    public TeamNotFoundException (String message){super(message);}
}
