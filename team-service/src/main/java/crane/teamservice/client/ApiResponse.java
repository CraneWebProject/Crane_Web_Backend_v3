package crane.teamservice.client;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ApiResponse<T> {

    private String status;
    private String message;
    private T data;

    @JsonCreator
    public ApiResponse(
            @JsonProperty("status") String status,
            @JsonProperty("message") String message,
            @JsonProperty("data") T data){
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public ApiResponse(String message, T data) {
        this.status = "success";
        this.message = message;
        this.data = data;
    }

    public ApiResponse(String message) {
        this.status = "success";
        this.message = message;
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(message, data);
    }

    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(message);
    }


}
