package sch.crane.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sch.crane.domain.common.advice.ApiResponse;
import sch.crane.domain.common.dto.CustomUserDetails;
import sch.crane.domain.user.dto.UserRequestDto;
import sch.crane.domain.user.dto.UserResponseDto;
import sch.crane.domain.user.service.UserService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponseDto>> signUp(@Valid @RequestBody UserRequestDto userRequestDto) {
        UserResponseDto user = userService.signUp(userRequestDto);
        return ResponseEntity.ok(ApiResponse.success("회원가입 성공", user));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@Valid @RequestBody UserRequestDto userRequestDto) {
        String token = userService.login(userRequestDto);
        return ResponseEntity.ok(ApiResponse.success("로그인 성공 ",token));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout(@RequestHeader("Authorization") String token) {
        userService.logout(token);
        return ResponseEntity.ok(new ApiResponse<>("로그아웃 성공"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<UserResponseDto>> getUsers(@AuthenticationPrincipal CustomUserDetails authUser) {
        UserResponseDto user = userService.myPage(authUser.getEmail());
        return ResponseEntity.ok(ApiResponse.success("마이페이지 조회 성공",user));
    }

    @PatchMapping("/delete")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@AuthenticationPrincipal CustomUserDetails authUser,
                                                        @Valid @RequestBody UserRequestDto userRequestDto) {
        userService.deleteUser(authUser.getEmail(),userRequestDto.getPassword());
        return ResponseEntity.ok(ApiResponse.success("탈퇴성공"));

    }
}
