package crane.userservice.service;

import com.sun.jdi.request.DuplicateRequestException;
import crane.userservice.common.config.JwtUtils;
import crane.userservice.common.config.PasswordEncoder;
import crane.userservice.dto.UserRequestDto;
import crane.userservice.dto.UserResponseDto;
import crane.userservice.entity.User;
import crane.userservice.entity.enums.UserRole;
import crane.userservice.exception.PasswordNotMatchException;
import crane.userservice.exception.UserNotFoundException;
import crane.userservice.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional
    public UserResponseDto signUp(UserRequestDto userRequestDto) {
        if(userRepository.existsByEmail(userRequestDto.getEmail())) {
            throw new DuplicateRequestException("중복된 이메일 입니다.");
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

        String accessToken = jwtUtils.createToken(user.getUserId(), user.getUserRole());
        String refreshToken = jwtUtils.createRefreshToken(user.getUserId(),user.getUserRole());
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
        String email = jwtUtils.getUserIdFromToken(accessToken);
        // 리프레시 토큰 삭제
        redisTemplate.delete("RT:" + email);
        // 액세스 토큰 블랙리스트 등록
        Claims claims = jwtUtils.extractClaims(accessToken);
        long expiration = claims.getExpiration().getTime() - System.currentTimeMillis();
        if (expiration > 0) {
            redisTemplate.opsForValue().set(
                    "BL:" + accessToken,
                    "logout",
                    expiration,
                    TimeUnit.MILLISECONDS);
        }

        return null;
    }


    @Cacheable(value = "userById", key = "#id", cacheManager = "redisCacheManager")
    public UserResponseDto myPage(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다."));
        return UserResponseDto.from(user);
    }

    @CacheEvict(value = "userById", key = "#id")
    @Transactional
    public UserResponseDto updateUser(Long id, UserRequestDto userRequestDto) {
        String newPassword = null;
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("유저를 찾을수 없습니다."));
        // 비밀번호가 동일하지 않으면 새 비밀번호로 인코딩, 동일하면 기존 비밀번호 유지
        if (!passwordEncoder.matches(userRequestDto.getPassword(), user.getPassword())) {
            newPassword = passwordEncoder.encode(userRequestDto.getPassword());
        } else {
            newPassword = user.getPassword();  // 기존 비밀번호 유지
        }
        user.updateUser(newPassword, userRequestDto.getDepartment(),
                userRequestDto.getSession(),userRequestDto.getPhone());
        return UserResponseDto.from(user);
    }

    @CacheEvict(value = "userById", key = "#id")
    @Transactional
    public void deleteUser(Long id, String password) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new PasswordNotMatchException("유저를 찾을수 없습니다"));
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new PasswordNotMatchException("비밀번호가 일치 하지않습니다.");
        }
        user.deleteAccount();
    }

    @Cacheable(value = "userById", key = "#id", cacheManager = "redisCacheManager")
    public UserResponseDto findById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("유저를 찾을 수 없습니다"));
        return UserResponseDto.from(user);
    }

    public List<UserResponseDto> findAll() {
        List<User> users = userRepository.findAll();

        // User 엔티티 리스트를 UserResponseDto 리스트로 변환
        return users.stream()
                .map(UserResponseDto::from) // UserResponseDto.from(User user) 메서드 사용
                .toList();
    }


}
