package crane.userservice.entity;

import crane.userservice.entity.enums.Session;
import crane.userservice.entity.enums.UserRole;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String email;

    private String password;

    private String name;

    private String department;

    private String studentId;

    private String phone;

    private String birth;

    @Enumerated(EnumType.STRING)
    private Session session;

    private Integer th;

    private UserRole userRole;

    private Boolean isDelete = false;

    private LocalDateTime deletedAt;

    @Builder
    public User(String email, String password, String name, String department, String studentId,
                String phone, String birth, Session session, Integer th, UserRole userRole, Boolean isDelete) {
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
        this.isDelete = isDelete;
    }

    public void updateUser(String password, String department,Session session,String phone){
        this.password = password;
        this.department = department;
        this.session = session;
        this.phone = phone;
    }
    public void  deleteAccount(){
        this.isDelete = true;
    }
}