package crane.notificationservice.service;


import crane.notificationservice.dto.TokenRequestDto;
import crane.notificationservice.entity.FcmToken;
import crane.notificationservice.repository.FcmTokenRepository;
import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class FcmTokenService {

    private final FcmTokenRepository fcmTokenRepository;


    @Transactional
    public void updateFcmToken(Long userId, TokenRequestDto dto) {
        //사용자Id로 List 검색
        //List 에서 같은 Token이 존재하면 pass
        LocalDateTime now = LocalDateTime.now();
        if(fcmTokenRepository.existsByFcmToken(dto.getFcmToken())){
            throw new EntityExistsException("FCM token already exists");
        }

        //사용자에 대한 토큰이 존재하지 않는 경우 새로 생성
        FcmToken newToken = FcmToken.builder()
                .fcmToken(dto.getFcmToken())
                .userId(userId)
                .laseUsedAt(now)
                .deviceInfo(dto.getDeviceInfo())
                .isActive(true)
                .build();
        fcmTokenRepository.save(newToken);

    }

    @Transactional
    public void deleteFcmToken(Long userId, String fcmToken) {
        Optional<FcmToken> optionalFcmToken =  fcmTokenRepository.findByUserIdAndFcmToken(userId, fcmToken);

        if (optionalFcmToken.isPresent()) {
            int deletedCount = fcmTokenRepository.deleteByFcmTokenAndUserId(userId, fcmToken);

            if(deletedCount == 0){
                throw new IllegalArgumentException("토큰이 존재하지 않거나 사용자에게 속해 있지 않습니다.");
            }
        }else {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }
    }

    public String getLatestFcmToken(Long userId) {
        return fcmTokenRepository.findLatestByUserId(userId)
                .map(FcmToken::getFcmToken)
                .orElse(null);
    }



}
