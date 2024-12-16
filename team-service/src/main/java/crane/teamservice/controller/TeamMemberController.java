package crane.teamservice.controller;

import crane.teamservice.client.ApiResponse;
import crane.teamservice.dto.TeamMemberRequestDto;
import crane.teamservice.dto.TeamMemberResponseDto;
import crane.teamservice.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class TeamMemberController {

    private final TeamService teamService;

    @PostMapping
    public ResponseEntity<ApiResponse<TeamMemberResponseDto>> createTeamMember(@RequestHeader("X-Authenticated-User") Long userId, @RequestBody TeamMemberRequestDto teamMemberRequestDto){
        TeamMemberResponseDto teamMember = teamService.createTeamMember(teamMemberRequestDto, userId);
        return ResponseEntity.ok(ApiResponse.success("팀원 생성 완료", teamMember));
    }

    @GetMapping("/{teamMemberId}")
    public ResponseEntity<ApiResponse<TeamMemberResponseDto>> readTeamMember(@PathVariable Long teamMemberId){
        TeamMemberResponseDto teamMember = teamService.readTeamMember(teamMemberId);
        return ResponseEntity.ok(ApiResponse.success("팀원 조회 완료", teamMember));
    }

    @DeleteMapping("/{teamMemberId}")
    public ResponseEntity<ApiResponse<TeamMemberResponseDto>> deleteTeamMember(@PathVariable Long teamMemberId){
        TeamMemberResponseDto teamMember = teamService.deleteTeamMember(teamMemberId);
        return ResponseEntity.ok(ApiResponse.success("팀원 삭제 완료", teamMember));
    }

    @GetMapping("/list/{teamId}")
    public ResponseEntity<ApiResponse<List<TeamMemberResponseDto>>> readAllTeamMembers(@PathVariable Long teamId){
        List<TeamMemberResponseDto> teamMemberlist = teamService.readAllTeamMember(teamId);
        return ResponseEntity.ok(ApiResponse.success("팀원 모두 조회 완료", teamMemberlist));
    }


}
