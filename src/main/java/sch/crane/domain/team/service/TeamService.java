package sch.crane.domain.team.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sch.crane.domain.team.dto.TeamMemberRequestDto;
import sch.crane.domain.team.dto.TeamMemberResponseDto;
import sch.crane.domain.team.dto.TeamRequestDto;
import sch.crane.domain.team.dto.TeamResponseDto;
import sch.crane.domain.team.entity.Team;
import sch.crane.domain.team.entity.TeamMember;
import sch.crane.domain.team.entity.enums.Role;
import sch.crane.domain.team.entity.enums.TeamType;
import sch.crane.domain.team.exception.TeamMemberNotfoundException;
import sch.crane.domain.team.exception.TeamNotFoundException;
import sch.crane.domain.team.repository.TeamMemberRepository;
import sch.crane.domain.team.repository.TeamRepository;
import sch.crane.domain.user.entity.User;
import sch.crane.domain.user.exception.UserNotFoundException;
import sch.crane.domain.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class TeamService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;

    //팀 생성
    @Transactional
    public TeamResponseDto createTeam(TeamRequestDto teamRequestDto) {
        TeamType teamType = Optional.ofNullable(teamRequestDto.getTeamType())
                .orElse(TeamType.FREE);

        Team team = Team.builder()
                .name(teamRequestDto.getTeamName())
                .teamType(teamType)
                .build();
        teamRepository.save(team);
        return TeamResponseDto.from(team);
    }

    //팀 멤버 생성
    @Transactional
    public TeamMemberResponseDto createTeamMember(TeamMemberRequestDto teamMemberRequestDto) {
        Team team = teamRepository.findByIdOrElseThrow(teamMemberRequestDto.getTeam_id());
        User user = userRepository.findById(teamMemberRequestDto.getUser_id())
                .orElseThrow(() -> new UserNotFoundException("사용자가 존재하지 않습니다."));

        Role role = Optional.ofNullable(teamMemberRequestDto.getRole())
                .orElse(Role.MEMBER);

        TeamMember teamMember = TeamMember.builder()
                .team(team)
                .user(user)
                .role(role)
                .build();
        teamMemberRepository.save(teamMember);
        return TeamMemberResponseDto.from(teamMember);
    }

    //팀 조회
    public TeamResponseDto findTeamById(Long team_id) {
        Team team = teamRepository.findByIdOrElseThrow(team_id);
        return TeamResponseDto.from(team);
    }

    //팀 목록
    public List<TeamResponseDto> findTeamList(){
        List<Team> teamList = teamRepository.findAll();

        return teamList.stream()
                .map(TeamResponseDto::from)
                .collect(Collectors.toList());
    }

    //멤버별 팀 멤버 목록
    public List<TeamMemberResponseDto> findTeamListByUserId(Long user_id) {
        userRepository.findById(user_id).orElseThrow(() -> new UserNotFoundException("사용자가 없습니다."));

        List<TeamMember> teamMemberList = teamMemberRepository.findByUserId(user_id);
        return teamMemberList.stream()
                .map(TeamMemberResponseDto::from)
                .collect(Collectors.toList());
    }

    //팀별 멤버 목록
    public List<TeamMemberResponseDto> findTeamMemberListByTeamId(Long team_id) {
        teamRepository.findByIdOrElseThrow(team_id);

        List<TeamMember> teamMemberList = teamMemberRepository.findByTeamId(team_id);
        return teamMemberList.stream()
                .map(TeamMemberResponseDto::from)
                .collect(Collectors.toList());
    }

    //팀 수정
    @Transactional
    public TeamResponseDto updateTeam(TeamRequestDto teamRequestDto) {
        Team team = teamRepository.findByIdOrElseThrow(teamRequestDto.getTeam_id());

        String teamName = teamRequestDto.getTeamName() != null
                ? teamRequestDto.getTeamName()
                : team.getName();

        TeamType teamType = teamRequestDto.getTeamType() != null
                ? teamRequestDto.getTeamType()
                :team.getTeamType();

        team.updateTeam(teamName, teamType);
        return TeamResponseDto.from(team);
    }


    //팀 멤버 수정
    @Transactional
    public TeamMemberResponseDto updateTeamMember(TeamMemberRequestDto teamMemberRequestDto) {
        TeamMember teamMember = teamMemberRepository.findById(teamMemberRequestDto.getTeamMember_id())
                .orElseThrow(() -> new TeamMemberNotfoundException("존재하지 않는 팀원입니다."));

        teamMember.updateTeamMember(teamMemberRequestDto.getRole());
        return TeamMemberResponseDto.from(teamMember);

    }

    //팀 삭제
    @Transactional
    public void deleteTeam(Long team_id) {
        Team team = teamRepository.findByIdOrElseThrow(team_id);
        teamRepository.delete(team);
    }

    //팀 멤버 삭제
    @Transactional
    public void deleteTeamMember(Long teamMember_id) {
        TeamMember teamMember = teamMemberRepository.findById(teamMember_id)
                .orElseThrow(() -> new TeamMemberNotfoundException("존재하지 않는 팀원입니다"));

        teamMemberRepository.delete(teamMember);
    }
}
