package crane.notificationservice.controller;

import crane.notificationservice.common.advice.ApiResponse;
import crane.notificationservice.dto.TokenRequestDto;
import crane.notificationservice.service.FcmService;
import crane.notificationservice.service.FcmTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/fcm")
@RequiredArgsConstructor
public class FcmTokenController {

    private final FcmTokenService fcmTokenService;

    @PostMapping
    public ResponseEntity<ApiResponse<?>> updateFcmToken(
            @RequestHeader("X-Authenticated-User") Long userId,
            @RequestBody TokenRequestDto dto) {
        try {
            fcmTokenService.updateFcmToken(userId,dto);
            return ResponseEntity.ok(ApiResponse.success("Successfully updated FCM token"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Error while updating FCM token"));
        }
    }

    @DeleteMapping("/{fcmToken}")
    public ResponseEntity<ApiResponse<?>> deleteFcmToken(
            @RequestHeader("X-Authenticated-User") Long userId,
            @PathVariable String fcmToken){
        fcmTokenService.deleteFcmToken(userId, fcmToken);
        return ResponseEntity.ok(ApiResponse.success("Successfully deleted FCM token"));
    }


    @GetMapping
    public ResponseEntity<ApiResponse<?>> getFcmToken(
            @RequestHeader("X-Authenticated-User") Long userId
    ){
        try {
            String latestToken = fcmTokenService.getLatestFcmToken(userId);
            return ResponseEntity.ok(ApiResponse.success(latestToken != null ? latestToken : ""));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.error("Failed to get FCM token: " + e.getMessage()));
        }
    }
}
