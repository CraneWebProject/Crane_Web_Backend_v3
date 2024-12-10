package crane.userservice.dto;

import crane.userservice.entity.enums.Session;
import crane.userservice.entity.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
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


}