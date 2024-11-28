package sch.crane.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sch.crane.domain.user.entity.enums.Session;
import sch.crane.domain.user.entity.enums.UserRole;

@Getter
@NoArgsConstructor
public class UserRequestDto {

    @Email(message = "유효한 이메일 형식이야 합니다")
    @Pattern(
            regexp = "^[A-Za-z0-9._%+-]+@sch\\.ac\\.kr$",
            message = "순천향대학교 이메일(@sch.ac.kr)만 사용 가능합니다"
    )
    private String email;

    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "비밀번호는 8자 이상의 영문, 숫자, 특수문자를 포함해야 합니다"
    )
    private String password;


    private String name;


    private String department;


    private String studentId;


    private String phone;


    private String birth;

    private Session session;

    private Integer th;

    private UserRole userRole;


    public UserRequestDto(String email, String password, String name, String department, String studentId, String phone, String birth, Session session, Integer th, UserRole userRole) {
        this.email = email;
        this.password = password;
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
