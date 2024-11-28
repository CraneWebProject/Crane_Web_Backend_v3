package sch.crane.domain.user.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sch.crane.domain.user.entity.enums.Session;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@Entity
public class User {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_id;

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

    private Boolean isDelete = false;

    private LocalDateTime deletedAt;

    @Builder
    public User(String email, String password, String name, String department, String studentId,
                String phone, String birth, Session session, Integer th, Boolean isDelete, LocalDateTime deletedAt) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.department = department;
        this.studentId = studentId;
        this.phone = phone;
        this.birth = birth;
        this.session = session;
        this.th = th;
        this.isDelete = isDelete;
        this.deletedAt = deletedAt;
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
