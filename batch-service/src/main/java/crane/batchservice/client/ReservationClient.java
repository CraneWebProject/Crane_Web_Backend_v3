package crane.batchservice.client;

import crane.batchservice.common.advice.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "reservation-service")
public interface ReservationClient {

    @PostMapping("/api/v1/reservations/init")
    ApiResponse<Void> initReservation();

    @PostMapping("/api/v1/reservations/init-nextweek")
    ApiResponse<Void> initNextWeekReservation();

    @GetMapping("/api/v1/reservations/open-ensemble")
    ApiResponse<Void> openEnsembleReservation();

    @GetMapping("/api/v1/reservations/open-inst")
    ApiResponse<Void> openInstReservation();

}
