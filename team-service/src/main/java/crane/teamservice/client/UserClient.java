package crane.teamservice.client;

import crane.teamservice.dto.UserResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping ("/api/v1/users/{userId}")
    ApiResponse<UserResponseDto> GetById(@PathVariable("userId") Long userId);
}
