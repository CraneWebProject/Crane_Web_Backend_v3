package crane.teamservice.service;

import crane.teamservice.client.UserClient;
import crane.teamservice.common.exceptions.BadRequestException;
import crane.teamservice.dto.*;
import crane.teamservice.entity.Team;
import crane.teamservice.entity.TeamMember;
import crane.teamservice.entity.enums.Role;
import crane.teamservice.exception.TeamMemberNotFoundException;
import crane.teamservice.exception.TeamNotFoundException;
import crane.teamservice.repository.TeamMemberRepository;
import crane.teamservice.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static crane.teamservice.entity.enums.Role.LEADER;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamService {
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserClient userClient;

    //팀 생성
    @Transactional
    public TeamResponseDto createTeam(TeamRequestDto teamRequestDto){
        Team team = Team.builder()
                .teamName(teamRequestDto.getTeamName())
                .teamType(teamRequestDto.getTeamType())
                .build();

        teamRepository.save(team);

        return TeamResponseDto.from(team);
    }

    //팀 조회
    public TeamResponseDto readTeam(Long teamId){
        Team team = teamRepository.findTeamByTeamId(teamId).orElseThrow(
                ()-> new TeamNotFoundException("팀을 찾을 수 없습니다."));
        return TeamResponseDto.from(team);
    }

    //전체 팀 목록 조회
    public List<TeamResponseDto> findTeamList(){
        List<Team> teamList = teamRepository.findAll();

        return teamList.stream().map(TeamResponseDto::from)
                .collect(Collectors.toList());
    }

    //팀 멤버 생성
    @Transactional
    public TeamMemberResponseDto createTeamMember(TeamMemberRequestDto teamMemberRequestDto, Long userId) {
        teamRepository.findTeamByTeamId(teamMemberRequestDto.getTeamId()).orElseThrow(
                ()-> new TeamNotFoundException("팀을 찾을 수 없습니다."));

        userClient.GetById(userId); // 실존하는 유저인지 찾는거 이렇게만 해도 되나요?

        if (teamMemberRequestDto.getRole() == Role.LEADER) {
            Optional<TeamMember> existingLeader = teamMemberRepository.findTeamLeaderByTeamId(
                    teamMemberRequestDto.getTeamId(), Role.LEADER
            );
            if (existingLeader.isPresent()) {
                throw new IllegalStateException("이미 리더가 존재합니다. 리더 역할을 선택할 수 없습니다.");
            }
        }

        Role role = Optional.ofNullable(teamMemberRequestDto.getRole()).orElse(Role.MEMBER);
        TeamMember teamMember = TeamMember.builder()
                .role(role)
                .userId(userId)
                .teamId(teamMemberRequestDto.getTeamId())
                .build();
        teamMemberRepository.save(teamMember);
        return TeamMemberResponseDto.from(teamMember);
    }

    // 팀 멤버 조회
    public TeamMemberResponseDto readTeamMember(Long teamMemberId){
        TeamMember teamMember = teamMemberRepository.findByTeamMemberId(teamMemberId).orElseThrow(
                ()-> new TeamMemberNotFoundException("팀 멤버를 찾을 수 없습니다."));

        return TeamMemberResponseDto.from(teamMember);
    }

    public List<TeamMemberResponseDto> readAllTeamMember(Long teamId){
        List<TeamMember> teamMemberList = teamMemberRepository.findByTeamId(teamId);
        return teamMemberList.stream().map(TeamMemberResponseDto::from).toList();
    }

    //팀 수정
    @Transactional
    public TeamResponseDto updateTeam(Long teamId, TeamRequestDto teamRequestDto){
        Team team = teamRepository.findTeamByTeamId(teamId).orElseThrow(
                ()-> new TeamNotFoundException("팀을 찾을 수 업습니다."));

        team.updateTeam(teamRequestDto.getTeamName(), teamRequestDto.getTeamType());
        return TeamResponseDto.from(team);
    }

    //팀 삭제
    @Transactional
    public TeamResponseDto deleteTeam(Long teamId, Long userId) {
        Team team = teamRepository.findTeamByTeamId(teamId).orElseThrow(
                () -> new TeamNotFoundException("팀을 찾을 수 없습니다."));
        TeamMember teamleader = teamMemberRepository.findTeamLeaderByTeamId(teamId, LEADER).orElseThrow(
                ()-> new TeamMemberNotFoundException("팀 리더를 찾을 수 없습니다."));
        UserResponseDto user = userClient.GetById(userId).getData();
        if(!userId.equals(teamleader.getUserId())) {
                throw new BadRequestException("삭제 권한이 없습니다. 팀 리더만 삭제가 가능합니다.");
        }
        teamRepository.deleteById(teamId);
        return TeamResponseDto.from(team);
    }

    // 팀 멤버 삭제
    @Transactional
    public TeamMemberResponseDto deleteTeamMember(Long teamMemberId){
        TeamMember teamMember = teamMemberRepository.findByTeamMemberId(teamMemberId).orElseThrow(
                () -> new TeamMemberNotFoundException("팀멤버를 찾을 수 없습니다."));

        teamMemberRepository.deleteById(teamMemberId);
        return TeamMemberResponseDto.from(teamMember);
    }

    // 팀 멤버 전체 삭제
    @Transactional
    public List<TeamMemberResponseDto> deleteAllTeamMembers(Long teamId){
        teamRepository.findTeamByTeamId(teamId).orElseThrow(
                ()-> new TeamNotFoundException("팀을 찾을 수 없습니다."));
        List<TeamMember> teamMemberList = teamMemberRepository.findByTeamId(teamId);
        teamMemberRepository.deleteByTeamId(teamId);

        return teamMemberList.stream()
                .map(TeamMemberResponseDto::from)
                .toList();
    }



}
