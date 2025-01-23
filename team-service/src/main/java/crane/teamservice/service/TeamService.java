package crane.teamservice.service;

import Tcom.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.hibernate.annotations.Cache;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
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
    private static final String TEAM_LIST_KEY = "teamList";
    private final ObjectMapper objectMapper;

    private final RedisTemplate<String, String> redisTemplate;
    //팀 생성
    @Caching(evict = {
            @CacheEvict(value = "teamById", key = "#result.teamId"),
            @CacheEvict(value = "teams")
    })<L
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
    @Cacheable(value = "teamById", key = "#teamId", cacheManager = "redisCacheManager")
    public TeamResponseDto readTeam(Long teamId){
        Team team = teamRepository.findTeamByTeamId(teamId).orElseThrow(
                ()-> new TeamNotFoundException("팀을 찾을 수 없습니다."));
        return TeamResponseDto.from(team);
    }

    //전체 팀 목록 조회
    // @Cacheable(value = "teams", key = "'allTeams'", cacheManager =  "redisCacheManager") //-> 인덱싱으로 처리
    public List<TeamResponseDto> findTeamList(){

        List<Team> teamList = teamRepository.findAll();

        return teamList.stream().map(TeamResponseDto::from).toList();
    }

    //팀 멤버 생성
    @Caching(evict =  {
            @CacheEvict(value = "teamMemberById", key = "#result.teamMemberId"),
            @CacheEvict(value = "teamMemberByTeamId", key = "#teamMemberRequestDto.teamId")
    })
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

    // 팀 멤버 정보 조회
    @Cacheable(value = "teamMemberById", key = "#teamMemberId", cacheManager = "redisCacheManager")
    public TeamMemberResponseDto readTeamMember(Long teamMemberId){
        TeamMember teamMember = teamMemberRepository.findByTeamMemberId(teamMemberId).orElseThrow(
                ()-> new TeamMemberNotFoundException("팀 멤버를 찾을 수 없습니다."));

        return TeamMemberResponseDto.from(teamMember);
    }

    // 팀 멤버 전체 조회
    @Cacheable(value = "teamMemberByTeamId", key = "#teamId", cacheManager = "redisCacheManager")

    public List<TeamMemberResponseDto> readAllTeamMember(Long teamId){
        List<TeamMember> teamMemberList = teamMemberRepository.findByTeamId(teamId);
        return teamMemberList.stream().map(TeamMemberResponseDto::from).toList();
    }

    //팀 정보 수정
    @CacheEvict(value = "teamById", key = "#teamId")
    @Transactional
    public TeamResponseDto updateTeam(Long teamId, TeamRequestDto teamRequestDto){
        Team team = teamRepository.findTeamByTeamId(teamId).orElseThrow(
                ()-> new TeamNotFoundException("팀을 찾을 수 업습니다."));

        team.updateTeam(teamRequestDto.getTeamName(), teamRequestDto.getTeamType());
        return TeamResponseDto.from(team);
    }

    //팀 삭제
    @CacheEvict(value = "teamById", key = "#teamId")
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
    @CacheEvict(value = "teamMemberById", key = "#teamMemberId")
    @Transactional
    public TeamMemberResponseDto deleteTeamMember(Long teamMemberId){
        TeamMember teamMember = teamMemberRepository.findByTeamMemberId(teamMemberId).orElseThrow(
                () -> new TeamMemberNotFoundException("팀멤버를 찾을 수 없습니다."));

        teamMemberRepository.deleteById(teamMemberId);
        return TeamMemberResponseDto.from(teamMember);
    }

    // 팀 멤버 전체 삭제
    @CacheEvict(value = "teamMemberByTeamId", key = "#teamId")
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
