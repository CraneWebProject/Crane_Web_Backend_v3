package sch.crane.domain.user.service;

import jakarta.persistence.Id;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sch.crane.domain.common.config.JwtUtils;
import sch.crane.domain.common.config.PasswordEncoder;
import sch.crane.domain.user.dto.UserRequestDto;
import sch.crane.domain.user.dto.UserResponseDto;
import sch.crane.domain.user.entity.User;
import sch.crane.domain.user.entity.enums.UserRole;
import sch.crane.domain.user.exception.PasswordNotMatchException;
import sch.crane.domain.user.exception.UserExistsException;
import sch.crane.domain.user.exception.UserNotFoundException;
import sch.crane.domain.user.repository.UserRepository;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public UserResponseDto signUp(UserRequestDto userRequestDto) {
        if(userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new UserExistsException("중복된 이메일 입니다.");
        }
        String password = passwordEncoder.encode(userRequestDto.getPassword());

        UserRole role = Optional.ofNullable(userRequestDto.getUserRole())
                .orElse(UserRole.USER);
        UserRole.of(String.valueOf(role));

        User user = User.builder()
                .email(userRequestDto.getEmail())
                .password(password)
                .name(userRequestDto.getName())
                .department(userRequestDto.getDepartment())
                .studentId(userRequestDto.getStudentId())
                .phone(userRequestDto.getPhone())
                .birth(userRequestDto.getBirth())
                .session(userRequestDto.getSession())
                .th(userRequestDto.getTh())
                .userRole(role)
                .build();
        userRepository.save(user);
        return UserResponseDto.from(user);
    }
    @Transactional
    public String login(UserRequestDto userRequestDto) {
        User user = userRepository.findByEmail(userRequestDto.getEmail())
                .orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다."));

        if (!passwordEncoder.matches(userRequestDto.getPassword(), user.getPassword())) {
            throw new PasswordNotMatchException("잘못된 비밀번호 입니다.");
        }

        String accessToken = jwtUtils.createToken(user.getEmail(), user.getUserRole());
        String refreshToken = jwtUtils.createRefreshToken(user.getEmail(),user.getUserRole());
        // Redis에 RefreshToken 저장
        redisTemplate.opsForValue().set(
                "RT:" + user.getEmail(),
                refreshToken,
                jwtUtils.getRefreshTokenExpirationTime(),
                TimeUnit.MILLISECONDS
        );
        return accessToken;
    }

    public String logout(String accessToken) {
        String email = jwtUtils.getUserEmailFromToken(accessToken);
        redisTemplate.delete("RT:" + email);
        jwtUtils.invalidToken(accessToken);
        return null;
    }


    public UserResponseDto myPage(String email){
        User user = userRepository.findByEmailOrElseThrow(email);
        return UserResponseDto.from(user);
    }

    @Transactional
    public void deleteUser(String email,String password) {
        User user = userRepository.findByEmailOrElseThrow(email);
        if(passwordEncoder.matches(password, user.getPassword())) {
            throw new PasswordNotMatchException("비밀번호가 일치 하지 않습니다");
        }
        user.deleteAccount();
    }



}
