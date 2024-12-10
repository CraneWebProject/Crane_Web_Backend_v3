package crane.userservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import crane.userservice.entity.User;
import crane.userservice.entity.enums.Session;
import crane.userservice.entity.enums.UserRole;
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
    private Session session;
    private Integer th;
    private UserRole userRole;

    @Builder
    private UserResponseDto(String email, String name, String department,
                            String studentId, String phone, String birth,
                            Session session, Integer th, UserRole userRole) {
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

    // 정적 팩토리 메서드
    public static UserResponseDto from(User user) {
        return UserResponseDto.builder()
                .email(user.getEmail())
                .name(user.getName())           // 실제 이름이 들어가도록 수정
                .department(user.getDepartment()) // 실제 학과가 들어가도록 수정
                .studentId(user.getStudentId())
                .phone(user.getPhone())
                .birth(user.getBirth())
                .session(user.getSession())
                .th(user.getTh())
                .userRole(user.getUserRole())
                .build();
    }
}