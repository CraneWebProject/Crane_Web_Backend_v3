package sch.crane.domain.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sch.crane.domain.common.dto.CustomUserDetails;
import sch.crane.domain.user.entity.User;
import sch.crane.domain.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmailOrElseThrow(email);

        //User 엔티티를 기반으로 객체를 변환할거야
        return new CustomUserDetails(user);
    }
}
