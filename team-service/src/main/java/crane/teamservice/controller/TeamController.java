package crane.teamservice.controller;

import crane.teamservice.client.ApiResponse;
import crane.teamservice.dto.TeamRequestDto;
import crane.teamservice.dto.TeamResponseDto;
import crane.teamservice.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/team")
public class TeamController {

    private final TeamService teamService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<TeamResponseDto>> createTeam(@RequestBody TeamRequestDto teamRequestDto){
        TeamResponseDto team = teamService.createTeam(teamRequestDto);
        return ResponseEntity.ok(ApiResponse.success("팀 생성완료", team));
    }

    @GetMapping("/{teamId}")
    public ResponseEntity<ApiResponse<TeamResponseDto>> readTeam(@PathVariable Long teamId){
        TeamResponseDto team = teamService.readTeam(teamId);
        return ResponseEntity.ok(ApiResponse.success("팀 조회 완료", team));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<TeamResponseDto>>> readAllTeam(){
        List<TeamResponseDto> teamList = teamService.findTeamList();
        return ResponseEntity.ok(ApiResponse.success("전체 팀 조회 완료", teamList));
    }

    @PutMapping("/{teamId}")
    public ResponseEntity<ApiResponse<TeamResponseDto>> updateTeam(@PathVariable Long teamId, @RequestBody TeamRequestDto teamRequestDto){
        TeamResponseDto team = teamService.updateTeam(teamId, teamRequestDto);
        return ResponseEntity.ok(ApiResponse.success("팀 수정완료", team));
    }

    @DeleteMapping("/{teamId}")
    public ResponseEntity<ApiResponse<TeamResponseDto>> deleteTeam(@RequestHeader("X-Authenticated-User") Long userId, @PathVariable Long teamId){
        TeamResponseDto team = teamService.deleteTeam(teamId,userId);
        teamService.deleteAllTeamMembers(teamId); // 팀 삭제할때 팀 멤버도 삭제
        return ResponseEntity.ok(ApiResponse.success("팀 삭제 완료", team));
    }
}
