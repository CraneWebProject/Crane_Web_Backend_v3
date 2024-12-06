package sch.crane.domain.team.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sch.crane.domain.common.advice.ApiResponse;
import sch.crane.domain.team.dto.TeamMemberResponseDto;
import sch.crane.domain.team.dto.TeamRequestDto;
import sch.crane.domain.team.dto.TeamResponseDto;
import sch.crane.domain.team.service.TeamService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/team")
public class TeamController {
    private final TeamService teamService;

    @PostMapping
    public ResponseEntity<ApiResponse<TeamResponseDto>> createTeam(@RequestBody TeamRequestDto teamRequestDto) {
        TeamResponseDto teamResponseDto = teamService.createTeam(teamRequestDto);
        return ResponseEntity.ok(ApiResponse.success("팀 생성 성공", teamResponseDto));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<TeamResponseDto>> updateTeam(@RequestBody TeamRequestDto teamRequestDto) {
        TeamResponseDto teamResponseDto = teamService.updateTeam(teamRequestDto);
        return ResponseEntity.ok(ApiResponse.success("팀 수정 성공", teamResponseDto));
    }

    @GetMapping("/{team_id}")
    public ResponseEntity<ApiResponse<TeamResponseDto>> getTeam(@PathVariable Long team_id) {
        TeamResponseDto teamResponseDto = teamService.findTeamById(team_id);
        return ResponseEntity.ok(ApiResponse.success("팀 조회 성공", teamResponseDto));
    }

    @DeleteMapping("/{team_id}")
    public ResponseEntity<ApiResponse<Void>> deleteTeam(@PathVariable Long team_id) {
        teamService.deleteTeam(team_id);
        return ResponseEntity.ok(ApiResponse.success("팀 삭제 성공"));
    }

    @GetMapping("/list")
    public ResponseEntity<ApiResponse<List<TeamResponseDto>>> getAllTeams() {
        List<TeamResponseDto> teamResponseDtoList = teamService.findTeamList();
        return ResponseEntity.ok(ApiResponse.success("팀 목록 조회 성공", teamResponseDtoList));
    }

    @GetMapping("/user/{user_id}")
    public ResponseEntity<ApiResponse<List<TeamMemberResponseDto>>> getTeamByUserId(@PathVariable Long user_id) {
        List<TeamMemberResponseDto> teamMemberResponseDtoList  = teamService.findTeamListByUserId(user_id);
        return ResponseEntity.ok(ApiResponse.success("팀 목록 조회 성공", teamMemberResponseDtoList));
    }


}
