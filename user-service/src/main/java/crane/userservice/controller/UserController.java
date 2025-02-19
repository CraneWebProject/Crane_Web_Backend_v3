package crane.userservice.controller;

import crane.userservice.common.advice.ApiResponse;
import crane.userservice.dto.UserRequestDto;
import crane.userservice.dto.UserResponseDto;
import crane.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponseDto>> signUp(
            @Valid @RequestBody UserRequestDto userRequestDto
    ) {
        UserResponseDto user = userService.signUp(userRequestDto);
        return ResponseEntity.ok(ApiResponse.success("회원가입 성공", user));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody UserRequestDto userRequestDto) {
        String accessToken = userService.login(userRequestDto);
        return ResponseEntity.ok()
                .header("Authorization", "Bearer " + accessToken)  // 헤더에 액세스 토큰 추가
                .body(ApiResponse.success("로그인 성공", accessToken));
    }
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String token) {
        userService.logout(token);
        return ResponseEntity.ok(new ApiResponse<>("로그아웃 성공"));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUsers(@RequestHeader("X-Authenticated-User") Long userId) {
        UserResponseDto user = userService.myPage(userId);
        return ResponseEntity.ok(
                ApiResponse.success("마이페이지 조회 성공", user)
        );
    }

    @PatchMapping("/my")
    public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(@RequestHeader("X-Authenticated-User") Long userId,
                                                                   @Valid @RequestBody UserRequestDto userRequestDto) {
        UserResponseDto user = userService.updateUser(userId, userRequestDto);

        return ResponseEntity.ok(
                ApiResponse.success("수정 성공 ", user)
        );
    }

    @PatchMapping("/delete")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@RequestHeader("X-Authenticated-User") Long userId,
                                                        @Valid @RequestBody UserRequestDto userRequestDto) {
        userService.deleteUser(userId, userRequestDto.getPassword());

        return ResponseEntity.ok(
                ApiResponse.success("탈퇴성공")
        );
    }

    // 1. 특정 사용자 정보 조회
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponseDto>> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "조회 성공", userService.findById(userId)
                ));
    }

    @GetMapping
    public List<UserResponseDto> getAllUsers() {
        return userService.findAll(); // 모든 사용자 정보 반환
    }


}
