package sch.crane.domain.team.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sch.crane.domain.common.advice.ApiResponse;
import sch.crane.domain.team.dto.TeamMemberRequestDto;
import sch.crane.domain.team.dto.TeamMemberResponseDto;
import sch.crane.domain.team.service.TeamService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/member")
public class TeamMemberController {
    private final TeamService teamService;

    @PostMapping
    public ResponseEntity<ApiResponse<TeamMemberResponseDto>> createTeamMember(@RequestBody TeamMemberRequestDto requestDto) {
        TeamMemberResponseDto responseDto = teamService.createTeamMember(requestDto);
        return ResponseEntity.ok(ApiResponse.success("멤버 생성 성공", responseDto));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<TeamMemberResponseDto>> updateTeamMember(@RequestBody TeamMemberRequestDto requestDto) {
        TeamMemberResponseDto responseDto = teamService.updateTeamMember(requestDto);
        return ResponseEntity.ok(ApiResponse.success("멤버 수정 성공", responseDto));
    }

    @GetMapping("/{team_id}")
    public ResponseEntity<ApiResponse<List<TeamMemberResponseDto>>> getTeamMember(@PathVariable Long team_id) {
        List<TeamMemberResponseDto> teamMemberResponseDtoList = teamService.findTeamMemberListByTeamId(team_id);
        return ResponseEntity.ok(ApiResponse.success("팀별 멤버 목록 조회 성공", teamMemberResponseDtoList));
    }


    @DeleteMapping("{teamMember_id}")
    public ResponseEntity<ApiResponse<Void>> deleteTeamMember(@PathVariable Long teamMember_id) {
        teamService.deleteTeamMember(teamMember_id);
        return ResponseEntity.ok(ApiResponse.success("팀멤버 삭제 성공"));
    }

}
