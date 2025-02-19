package crane.userservice.entity.enums;
import crane.userservice.common.exceptions.NotFoundException;

import java.lang.reflect.Array;
import java.util.Arrays;
public enum UserRole {
    ADMIN,
    MANAGER,
    USER;

    public static UserRole of(String role){
        return Arrays.stream(UserRole.values())
                .filter(u -> u.toString().equals(role)).
                findFirst()
                .orElseThrow(()-> new NotFoundException("유효 하지 않은 권한 입니다."));
    }
}