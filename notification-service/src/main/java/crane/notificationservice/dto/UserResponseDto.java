package crane.notificationservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class UserResponseDto {
    private String email;
    private String name;
    private String department;
    private String studentId;
    private String phone;
    private String birth;
    private String  session;
    private Integer th;
    private String userRole;

    @Builder
    public UserResponseDto(String email, String name, String department, String studentId, String phone, String birth, String session, Integer th, String userRole) {
        this.email = email;
        this.name = name;
        this.department = department;
        this.studentId = studentId;
        this.phone = phone;
        this.birth = birth;
        this.session = session;
        this.th = th;
        this.userRole = userRole;
    }
}
