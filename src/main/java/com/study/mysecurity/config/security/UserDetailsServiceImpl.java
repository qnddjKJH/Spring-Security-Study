package com.study.mysecurity.config.security;

import com.study.mysecurity.domain.user.User;
import com.study.mysecurity.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 email 을 가진 user 가 존재하지 않습니다."));
        Set<GrantedAuthority> grantedAuthorities = new HashSet<>();

        return new org
                .springframework
                .security
                .core
                .userdetails
                .User(user.getEmail(), user.getPassword(), grantedAuthorities);
    }
}
